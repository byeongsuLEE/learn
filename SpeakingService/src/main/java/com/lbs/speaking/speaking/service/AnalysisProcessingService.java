package com.lbs.speaking.speaking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.speaking.infrastructure.entity.AnalysisEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import com.lbs.speaking.speaking.infrastructure.repository.AnalysisJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.SpeakingRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalysisProcessingService {

    private final SpeakingRecordJpaRepository recordRepository;
    private final AnalysisJpaRepository analysisRepository;
    private final LlmAnalysisPort llmAnalysisPort;
    private final IssueIndexCorrector issueIndexCorrector;
    private final ProgressService progressService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(Long recordId) {
        SpeakingRecordEntity record = recordRepository.findByIdAndDeletedAtIsNull(recordId)
                .orElseThrow();
        if (record.getStatus() == SpeakingRecordStatus.COMPLETED) {
            return;
        }

        progressService.setProgress(recordId, 70);
        LlmAnalysisPort.LlmAnalysisResult result = llmAnalysisPort.analyze(
                record.getDailyQuestion().getQuestion().getContent(),
                record.getOriginalText()
        );
        progressService.setProgress(recordId, 90);

        var correctedIssues = issueIndexCorrector.correct(record.getOriginalText(), result.issues());
        AnalysisEntity analysis = analysisRepository.findByRecordId(recordId)
                .orElseGet(() -> AnalysisEntity.create(record, result.improvedText(), "[]", "[]"));
        analysis.replace(result.improvedText(), toJson(correctedIssues), toJson(result.renderBlocks()));
        analysisRepository.save(analysis);
        record.markCompleted();
        progressService.setProgress(recordId, 100);
    }

    @Transactional
    public void fail(Long recordId, String reason) {
        recordRepository.findByIdAndDeletedAtIsNull(recordId).ifPresent(record -> {
            record.markFailed(reason);
            progressService.setProgress(recordId, 100);
        });
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
