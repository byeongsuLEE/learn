package com.lbs.speaking.speaking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.SpeakingProperties;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadSessionService {

    private static final String PREFIX = "speaking:upload:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SpeakingProperties properties;

    public UploadSession create(String uploadId, Long userId, Long dailyQuestionId, String objectKey,
                                String mimeType, long sizeBytes, int durationSec) {
        UploadSession session = new UploadSession(
                uploadId,
                userId,
                dailyQuestionId,
                objectKey,
                mimeType,
                sizeBytes,
                durationSec,
                Instant.now().plus(properties.uploadSessionTtl())
        );
        redisTemplate.opsForValue().set(key(uploadId), toJson(session), properties.uploadSessionTtl());
        return session;
    }

    public UploadSession getRequired(String uploadId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(uploadId)))
                .map(this::fromJson)
                .orElseThrow(() -> new BusinessException(ErrorCode.UPLOAD_SESSION_NOT_FOUND));
    }

    public void delete(String uploadId) {
        redisTemplate.delete(key(uploadId));
    }

    private String key(String uploadId) {
        return PREFIX + uploadId;
    }

    private String toJson(UploadSession session) {
        try {
            return objectMapper.writeValueAsString(session);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private UploadSession fromJson(String value) {
        try {
            return objectMapper.readValue(value, UploadSession.class);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ErrorCode.INVALID_UPLOAD_SESSION, exception);
        }
    }
}
