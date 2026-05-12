package com.lbs.speaking.speaking.service;

import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private static final Duration TTL = Duration.ofHours(2);
    private static final String PREFIX = "speaking:progress:";

    private final StringRedisTemplate redisTemplate;

    public void setProgress(Long recordId, int progress) {
        redisTemplate.opsForValue().set(key(recordId), String.valueOf(progress), TTL);
    }

    public int getProgress(SpeakingRecordEntity record) {
        String value = redisTemplate.opsForValue().get(key(record.getId()));
        if (value != null) {
            return Integer.parseInt(value);
        }
        return fallback(record.getStatus());
    }

    public int fallback(SpeakingRecordStatus status) {
        return switch (status) {
            case PENDING_ANALYSIS -> 55;
            case ANALYZING -> 70;
            case COMPLETED, FAILED -> 100;
        };
    }

    private String key(Long recordId) {
        return PREFIX + recordId;
    }
}
