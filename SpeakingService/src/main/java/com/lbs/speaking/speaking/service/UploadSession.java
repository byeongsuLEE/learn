package com.lbs.speaking.speaking.service;

import java.time.Instant;

public record UploadSession(
        String uploadId,
        Long userId,
        Long dailyQuestionId,
        String objectKey,
        String mimeType,
        long sizeBytes,
        int durationSec,
        Instant expiresAt
) {
}
