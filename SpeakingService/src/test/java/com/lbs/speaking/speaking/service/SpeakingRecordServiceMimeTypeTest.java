package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.lbs.speaking.config.MinioProperties;
import com.lbs.speaking.config.SpeakingProperties;
import com.lbs.speaking.speaking.dto.SpeakingDtos;
import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.QuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingQuestionType;
import com.lbs.speaking.speaking.infrastructure.repository.AnalysisJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.DailyQuestionJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.SpeakingRecordJpaRepository;
import com.lbs.speaking.speaking.worker.AnalysisPublisher;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class SpeakingRecordServiceMimeTypeTest {

    private final SpeakingProperties speakingProperties = new SpeakingProperties(
            "Asia/Seoul",
            10,
            20_971_520,
            180,
            List.of("audio/webm"),
            new SpeakingProperties.Rabbit("exchange", "queue", "routing", "dlx", "dlq", "dlq-routing"),
            new SpeakingProperties.Jobs("0 */5 * * * *", "0 0 * * * *", 15, 24),
            new SpeakingProperties.Gemini(false, "https://generativelanguage.googleapis.com", "", "gemini-2.5-flash-lite", 30)
    );

    @Test
    void createUploadUrlAcceptsBrowserMimeTypeWithCodecSuffix() {
        TodayQuestionService todayQuestionService = mock(TodayQuestionService.class);
        UploadSessionService uploadSessionService = mock(UploadSessionService.class);
        AudioStorageService audioStorageService = mock(AudioStorageService.class);
        DailyQuestionEntity dailyQuestion = dailyQuestion();
        when(todayQuestionService.getOrCreateTodayQuestion(SpeakingQuestionType.DAILY)).thenReturn(dailyQuestion);
        when(audioStorageService.createUploadUrl("speaking/temp/2/upload-1.webm")).thenReturn("https://minio.local/upload-1");

        ObjectKeyGenerator objectKeyGenerator = mock(ObjectKeyGenerator.class);
        when(objectKeyGenerator.tempKey(eq(2L), anyString(), eq("audio/webm"))).thenReturn("speaking/temp/2/upload-1.webm");
        SpeakingRecordService service = new SpeakingRecordService(
                speakingProperties,
                new MinioProperties("http://localhost:9000", "http://localhost:9000", "minio", "secret", "bucket", 10, 10),
                todayQuestionService,
                mock(DailyQuestionJpaRepository.class),
                mock(SpeakingRecordJpaRepository.class),
                mock(AnalysisJpaRepository.class),
                uploadSessionService,
                audioStorageService,
                objectKeyGenerator,
                mock(ProgressService.class),
                mock(AnalysisPublisher.class),
                mock(AnalysisLimitService.class)
        );

        SpeakingDtos.PresignedUploadResponse response = service.createUploadUrl(
                2L,
                new SpeakingDtos.PresignedUploadRequest("audio/webm;codecs=opus", 1024, 30)
        );

        assertThat(response.objectKey()).isEqualTo("speaking/temp/2/upload-1.webm");
        verify(uploadSessionService).create(
                response.uploadId(),
                2L,
                5L,
                "speaking/temp/2/upload-1.webm",
                "audio/webm",
                1024,
                30
        );
    }

    private DailyQuestionEntity dailyQuestion() {
        QuestionEntity question = BeanUtils.instantiateClass(QuestionEntity.class);
        ReflectionTestUtils.setField(question, "content", "What did you do today?");
        DailyQuestionEntity dailyQuestion = DailyQuestionEntity.create(LocalDate.of(2026, 5, 13), question);
        ReflectionTestUtils.setField(dailyQuestion, "id", 5L);
        return dailyQuestion;
    }
}
