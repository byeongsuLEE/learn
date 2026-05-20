package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lbs.speaking.config.MinioProperties;
import com.lbs.speaking.config.SpeakingProperties;
import com.lbs.speaking.speaking.dto.SpeakingDtos;
import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.QuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingQuestionType;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordType;
import com.lbs.speaking.speaking.infrastructure.repository.AnalysisJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.DailyQuestionJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.SpeakingRecordJpaRepository;
import com.lbs.speaking.speaking.worker.AnalysisPublisher;
import com.lbs.speaking.speaking.worker.AnalysisRequestedEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class SpeakingRecordServiceTest {

    private final TodayQuestionService todayQuestionService = mock(TodayQuestionService.class);
    private final DailyQuestionJpaRepository dailyQuestionRepository = mock(DailyQuestionJpaRepository.class);
    private final SpeakingRecordJpaRepository recordRepository = mock(SpeakingRecordJpaRepository.class);
    private final AnalysisJpaRepository analysisRepository = mock(AnalysisJpaRepository.class);
    private final UploadSessionService uploadSessionService = mock(UploadSessionService.class);
    private final AudioStorageService audioStorageService = mock(AudioStorageService.class);
    private final ObjectKeyGenerator objectKeyGenerator = mock(ObjectKeyGenerator.class);
    private final ProgressService progressService = mock(ProgressService.class);
    private final AnalysisPublisher analysisPublisher = mock(AnalysisPublisher.class);
    private final AnalysisLimitService analysisLimitService = mock(AnalysisLimitService.class);
    private final SpeakingRecordService service = new SpeakingRecordService(
            properties(),
            minioProperties(),
            todayQuestionService,
            dailyQuestionRepository,
            recordRepository,
            analysisRepository,
            uploadSessionService,
            audioStorageService,
            objectKeyGenerator,
            progressService,
            analysisPublisher,
            analysisLimitService
    );

    @Test
    void createRecordCountsInitialAnalysisAndPublishesNonForceEvent() {
        DailyQuestionEntity dailyQuestion = dailyQuestion();
        UploadSession session = session(5L);
        when(uploadSessionService.getRequired("upload-1")).thenReturn(session);
        when(dailyQuestionRepository.findById(5L)).thenReturn(Optional.of(dailyQuestion));
        when(recordRepository.findByUserIdAndDailyQuestionIdAndDeletedAtIsNull(2L, 5L)).thenReturn(Optional.empty());
        when(recordRepository.save(any(SpeakingRecordEntity.class))).thenAnswer(invocation -> {
            SpeakingRecordEntity record = invocation.getArgument(0);
            ReflectionTestUtils.setField(record, "id", 10L);
            return record;
        });
        when(objectKeyGenerator.permanentKey(2L, 10L, "audio/webm")).thenReturn("speaking/audio/2/2026-05/10.webm");
        when(analysisRepository.findByRecordId(10L)).thenReturn(Optional.empty());

        SpeakingDtos.RecordResponse response = service.createRecord(2L, new SpeakingDtos.CreateRecordRequest(
                "upload-1",
                "speaking/temp/2/upload-1.webm",
                "I practiced today."
        ));

        assertThat(response.status()).isEqualTo(SpeakingRecordStatus.ANALYZING);
        verify(analysisLimitService).acquire(eq(2L), any(LocalDate.class));
        verify(analysisPublisher).publish(new AnalysisRequestedEvent(10L, 2L, 1, false));
    }

    @Test
    void createRecordReplacesExistingDailyRecordAndPublishesForceEvent() {
        DailyQuestionEntity dailyQuestion = dailyQuestion();
        UploadSession session = session(5L);
        SpeakingRecordEntity existing = questionRecord(20L, SpeakingRecordStatus.COMPLETED);
        when(uploadSessionService.getRequired("upload-1")).thenReturn(session);
        when(dailyQuestionRepository.findById(5L)).thenReturn(Optional.of(dailyQuestion));
        when(recordRepository.findByUserIdAndDailyQuestionIdAndDeletedAtIsNull(2L, 5L)).thenReturn(Optional.of(existing));
        when(objectKeyGenerator.permanentKey(2L, 20L, "audio/webm")).thenReturn("speaking/audio/2/2026-05/20.webm");
        when(analysisRepository.findByRecordId(20L)).thenReturn(Optional.empty());

        SpeakingDtos.RecordResponse response = service.createRecord(2L, new SpeakingDtos.CreateRecordRequest(
                "upload-1",
                "speaking/temp/2/upload-1.webm",
                "I recorded a better answer."
        ));

        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.originalText()).isEqualTo("I recorded a better answer.");
        assertThat(response.status()).isEqualTo(SpeakingRecordStatus.ANALYZING);
        verify(recordRepository, never()).save(any(SpeakingRecordEntity.class));
        verify(analysisLimitService).acquire(eq(2L), any(LocalDate.class));
        verify(analysisPublisher).publish(new AnalysisRequestedEvent(20L, 2L, 1, true));
    }

    @Test
    void reanalyzeCountsAnalysisAndPublishesForceEventStartingAtFirstAttempt() {
        SpeakingRecordEntity record = questionRecord(20L, SpeakingRecordStatus.COMPLETED);
        when(recordRepository.findByIdAndDeletedAtIsNull(20L)).thenReturn(Optional.of(record));
        when(analysisRepository.findByRecordId(20L)).thenReturn(Optional.empty());

        SpeakingDtos.RecordResponse response = service.reanalyze(2L, 20L);

        assertThat(response.status()).isEqualTo(SpeakingRecordStatus.ANALYZING);
        verify(analysisLimitService).acquire(eq(2L), any(LocalDate.class));
        verify(analysisPublisher).publish(new AnalysisRequestedEvent(20L, 2L, 1, true));
    }

    @Test
    void createCustomRecordStoresRecordedAudioWithoutAnalysis() {
        UploadSession session = session(null);
        when(uploadSessionService.getRequired("upload-custom-1")).thenReturn(session);
        when(recordRepository.save(any(SpeakingRecordEntity.class))).thenAnswer(invocation -> {
            SpeakingRecordEntity record = invocation.getArgument(0);
            ReflectionTestUtils.setField(record, "id", 30L);
            return record;
        });
        when(objectKeyGenerator.permanentKey(2L, 30L, "audio/webm")).thenReturn("speaking/audio/2/2026-05/30.webm");
        when(analysisRepository.findByRecordId(30L)).thenReturn(Optional.empty());

        SpeakingDtos.RecordResponse response = service.createCustomRecord(2L, new SpeakingDtos.CreateCustomRecordRequest(
                "upload-custom-1",
                "speaking/temp/2/upload-1.webm",
                "내가 직접 쓴 연습 글",
                "내가 직접 쓴 연습 글"
        ));

        assertThat(response.dailyQuestionId()).isNull();
        assertThat(response.question()).isEqualTo("내가 직접 쓴 연습 글");
        assertThat(response.recordType()).isEqualTo(SpeakingRecordType.CUSTOM_TEXT);
        assertThat(response.status()).isEqualTo(SpeakingRecordStatus.RECORDED);
        assertThat(response.analysis()).isNull();
        verify(analysisLimitService, never()).acquire(anyLong(), any(LocalDate.class));
        verify(analysisPublisher, never()).publish(any());
    }

    @Test
    void createCustomUploadUrlDoesNotCreateDailyQuestion() {
        when(objectKeyGenerator.tempKey(eq(2L), anyString(), eq("audio/webm"))).thenReturn("speaking/temp/2/upload-fixed.webm");
        when(audioStorageService.createUploadUrl("speaking/temp/2/upload-fixed.webm")).thenReturn("https://minio.local/upload");

        SpeakingDtos.PresignedUploadResponse response = service.createCustomUploadUrl(2L, new SpeakingDtos.PresignedUploadRequest(
                "audio/webm",
                100L,
                10
        ));

        assertThat(response.uploadUrl()).isEqualTo("https://minio.local/upload");
        verify(todayQuestionService, never()).getOrCreateTodayQuestion(any());
    }

    private UploadSession session(Long dailyQuestionId) {
        return new UploadSession(
                "upload-1",
                2L,
                dailyQuestionId,
                "speaking/temp/2/upload-1.webm",
                "audio/webm",
                100L,
                10,
                Instant.now().plusSeconds(600)
        );
    }

    private SpeakingRecordEntity questionRecord(Long id, SpeakingRecordStatus status) {
        SpeakingRecordEntity record = SpeakingRecordEntity.createPending(
                2L,
                dailyQuestion(),
                "speaking/audio/2/2026-05/20.webm",
                "I practiced today.",
                "audio/webm",
                100L,
                10
        );
        ReflectionTestUtils.setField(record, "id", id);
        ReflectionTestUtils.setField(record, "status", status);
        return record;
    }

    private DailyQuestionEntity dailyQuestion() {
        QuestionEntity question = BeanUtils.instantiateClass(QuestionEntity.class);
        ReflectionTestUtils.setField(question, "id", 1L);
        ReflectionTestUtils.setField(question, "content", "What did you practice today?");
        DailyQuestionEntity dailyQuestion = DailyQuestionEntity.create(LocalDate.of(2026, 5, 14), SpeakingQuestionType.DAILY, question);
        ReflectionTestUtils.setField(dailyQuestion, "id", 5L);
        return dailyQuestion;
    }

    private SpeakingProperties properties() {
        return new SpeakingProperties(
                "Asia/Seoul",
                10,
                52_428_800L,
                300,
                List.of("audio/webm"),
                new SpeakingProperties.Rabbit("exchange", "queue", "routing", "dlx", "dlq", "failed"),
                new SpeakingProperties.Jobs("0 */10 * * * *", "0 0 * * * *", 15, 24),
                new SpeakingProperties.Gemini(false, "https://generativelanguage.googleapis.com", "", "gemini-2.5-flash-lite", 30)
        );
    }

    private MinioProperties minioProperties() {
        return new MinioProperties("http://minio:9000", "http://minio:9000", "access", "secret", "speaking-audio", 10, 10);
    }
}
