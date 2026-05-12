package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.speaking.infrastructure.entity.AnalysisEntity;
import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.QuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import com.lbs.speaking.speaking.infrastructure.repository.AnalysisJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.SpeakingRecordJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

class AnalysisProcessingServiceTest {

    private final SpeakingRecordJpaRepository recordRepository = mock(SpeakingRecordJpaRepository.class);
    private final AnalysisJpaRepository analysisRepository = mock(AnalysisJpaRepository.class);
    private final LlmAnalysisPort llmAnalysisPort = mock(LlmAnalysisPort.class);
    private final IssueIndexCorrector issueIndexCorrector = mock(IssueIndexCorrector.class);
    private final ProgressService progressService = mock(ProgressService.class);
    private final AnalysisProcessingService processingService = new AnalysisProcessingService(
            recordRepository,
            analysisRepository,
            llmAnalysisPort,
            issueIndexCorrector,
            progressService,
            new ObjectMapper()
    );

    @Test
    void processDoesNothingWhenRecordIsAlreadyCompleted() {
        SpeakingRecordEntity record = record(10L, SpeakingRecordStatus.COMPLETED);
        when(recordRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(record));

        processingService.process(10L);

        verifyNoInteractions(llmAnalysisPort, issueIndexCorrector, analysisRepository, progressService);
    }

    @Test
    void processSavesAnalysisAndMarksRecordCompleted() {
        SpeakingRecordEntity record = record(10L, SpeakingRecordStatus.ANALYZING);
        LlmAnalysisPort.Issue issue = new LlmAnalysisPort.Issue(
                "GRAMMAR",
                0,
                4,
                "I go",
                "I went",
                "Past tense is needed.",
                true
        );
        LlmAnalysisPort.RenderBlock block = new LlmAnalysisPort.RenderBlock("improved", "I went", "blue", 0);
        LlmAnalysisPort.LlmAnalysisResult result = new LlmAnalysisPort.LlmAnalysisResult(
                "I went home.",
                List.of(issue),
                List.of(block)
        );
        when(recordRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(record));
        when(llmAnalysisPort.analyze("What did you do today?", "I go home.")).thenReturn(result);
        when(issueIndexCorrector.correct("I go home.", result.issues())).thenReturn(List.of(issue));
        when(analysisRepository.findByRecordId(10L)).thenReturn(Optional.empty());

        processingService.process(10L);

        ArgumentCaptor<AnalysisEntity> analysisCaptor = ArgumentCaptor.forClass(AnalysisEntity.class);
        verify(analysisRepository).save(analysisCaptor.capture());
        assertThat(analysisCaptor.getValue().getImprovedText()).isEqualTo("I went home.");
        assertThat(analysisCaptor.getValue().getIssuesJson()).contains("Past tense is needed.");
        assertThat(analysisCaptor.getValue().getRenderBlocksJson()).contains("improved");
        assertThat(record.getStatus()).isEqualTo(SpeakingRecordStatus.COMPLETED);
        verify(progressService).setProgress(10L, 70);
        verify(progressService).setProgress(10L, 90);
        verify(progressService).setProgress(10L, 100);
    }

    @Test
    void failMarksRecordFailedAndCompletesProgressWhenRecordExists() {
        SpeakingRecordEntity record = record(10L, SpeakingRecordStatus.ANALYZING);
        when(recordRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(record));

        processingService.fail(10L, "timeout");

        assertThat(record.getStatus()).isEqualTo(SpeakingRecordStatus.FAILED);
        assertThat(record.getFailureReason()).isEqualTo("timeout");
        verify(progressService).setProgress(10L, 100);
    }

    @Test
    void failIsIdempotentWhenRecordIsMissing() {
        when(recordRepository.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

        processingService.fail(10L, "timeout");

        verify(progressService, never()).setProgress(any(), any(Integer.class));
    }

    private SpeakingRecordEntity record(Long id, SpeakingRecordStatus status) {
        QuestionEntity question = BeanUtils.instantiateClass(QuestionEntity.class);
        ReflectionTestUtils.setField(question, "content", "What did you do today?");
        DailyQuestionEntity dailyQuestion = DailyQuestionEntity.create(LocalDate.of(2026, 5, 12), question);
        SpeakingRecordEntity record = SpeakingRecordEntity.createPending(
                2L,
                dailyQuestion,
                "temp.webm",
                "I go home.",
                "audio/webm",
                100L,
                10
        );
        ReflectionTestUtils.setField(record, "id", id);
        ReflectionTestUtils.setField(record, "status", status);
        return record;
    }
}
