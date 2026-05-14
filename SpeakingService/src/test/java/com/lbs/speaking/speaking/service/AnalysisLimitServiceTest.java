package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class AnalysisLimitServiceTest {

    private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private final AnalysisLimitService limitService = new AnalysisLimitService(redisTemplate);

    @Test
    void incrementsDailyCounterByUserAndSetsTtlOnFirstAttempt() {
        String key = "speaking:analysis:2:2026-05-12";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(1L);

        int attempt = limitService.acquire(2L, LocalDate.of(2026, 5, 12));

        assertThat(attempt).isEqualTo(1);
        verify(redisTemplate).expire(key, Duration.ofDays(2));
    }

    @Test
    void throwsWhenDailyLimitIsExceeded() {
        String key = "speaking:analysis:2:2026-05-12";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(6L);

        assertThatThrownBy(() -> limitService.acquire(2L, LocalDate.of(2026, 5, 12)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ANALYSIS_LIMIT_EXCEEDED);
    }

    @Test
    void allowsFifthAttemptWithoutResettingTtl() {
        String key = "speaking:analysis:2:2026-05-12";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(5L);

        int attempt = limitService.acquire(2L, LocalDate.of(2026, 5, 12));

        assertThat(attempt).isEqualTo(5);
        verify(redisTemplate, never()).expire(key, Duration.ofDays(2));
    }

    @Test
    void treatsNullIncrementResultAsFirstAttempt() {
        String key = "speaking:analysis:2:2026-05-12";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(key)).thenReturn(null);

        int attempt = limitService.acquire(2L, LocalDate.of(2026, 5, 12));

        assertThat(attempt).isEqualTo(1);
        verify(redisTemplate, never()).expire(key, Duration.ofDays(2));
    }
}
