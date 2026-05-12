package com.lbs.speaking.speaking.dto;

import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class SpeakingDtos {

    private SpeakingDtos() {
    }

    public record TodayQuestionResponse(
            Long dailyQuestionId,
            LocalDate questionDate,
            Long questionId,
            String content
    ) {
    }

    public record PresignedUploadRequest(
            @NotBlank String mimeType,
            @Positive long sizeBytes,
            @Positive int durationSec
    ) {
    }

    public record PresignedUploadResponse(
            String uploadId,
            String uploadUrl,
            String objectKey,
            int expiresInSec
    ) {
    }

    public record CreateRecordRequest(
            @NotBlank String uploadId,
            @NotBlank String objectKey,
            @NotBlank String transcript
    ) {
    }

    public record ProgressResponse(
            Long recordId,
            SpeakingRecordStatus status,
            int progress,
            String message
    ) {
    }

    public record RecordResponse(
            Long id,
            Long dailyQuestionId,
            String question,
            String originalText,
            SpeakingRecordStatus status,
            String failureReason,
            LocalDateTime createdAt,
            AnalysisResponse analysis
    ) {
    }

    public record RecordListItemResponse(
            Long id,
            String question,
            SpeakingRecordStatus status,
            LocalDateTime createdAt
    ) {
    }

    public record AnalysisResponse(
            String improvedText,
            String issuesJson,
            String renderBlocksJson
    ) {
    }

    public record AudioUrlResponse(String audioUrl, int expiresInSec) {
    }

    public record ReanalyzeRequest(@NotNull Boolean force) {
    }

    public record RecordListResponse(List<RecordListItemResponse> records) {
    }
}
