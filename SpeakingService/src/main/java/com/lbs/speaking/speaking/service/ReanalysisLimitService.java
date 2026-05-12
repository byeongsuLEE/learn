package com.lbs.speaking.speaking.service;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import java.time.Duration;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReanalysisLimitService {

    private static final int DAILY_LIMIT = 5;
    private static final Duration COUNTER_TTL = Duration.ofDays(2);

    private final StringRedisTemplate redisTemplate;

    public int acquire(Long userId, Long recordId, LocalDate today) {
        String key = key(userId, recordId, today);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, COUNTER_TTL);
        }
        if (count != null && count > DAILY_LIMIT) {
            throw new BusinessException(ErrorCode.REANALYZE_LIMIT_EXCEEDED);
        }
        return count == null ? 1 : count.intValue();
    }

    private String key(Long userId, Long recordId, LocalDate today) {
        return "speaking:reanalyze:%d:%d:%s".formatted(userId, recordId, today);
    }
}
