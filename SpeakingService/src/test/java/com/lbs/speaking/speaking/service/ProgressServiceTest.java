package com.lbs.speaking.speaking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

class ProgressServiceTest {

    private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private final ProgressService progressService = new ProgressService(redisTemplate);

    @Test
    void storesProgressWithTwoHourTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        progressService.setProgress(10L, 90);

        verify(valueOperations).set("speaking:progress:10", "90", Duration.ofHours(2));
    }

    @Test
    void returnsRedisProgressWhenPresent() {
        SpeakingRecordEntity record = record(10L, SpeakingRecordStatus.ANALYZING);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("speaking:progress:10")).thenReturn("88");

        int progress = progressService.getProgress(record);

        assertThat(progress).isEqualTo(88);
    }

    @Test
    void fallsBackFromRecordStatusWhenRedisValueIsMissing() {
        SpeakingRecordEntity record = record(10L, SpeakingRecordStatus.COMPLETED);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("speaking:progress:10")).thenReturn(null);

        int progress = progressService.getProgress(record);

        assertThat(progress).isEqualTo(100);
    }

    @Test
    void fallbackMapsEveryStatus() {
        assertThat(progressService.fallback(SpeakingRecordStatus.PENDING_ANALYSIS)).isEqualTo(55);
        assertThat(progressService.fallback(SpeakingRecordStatus.ANALYZING)).isEqualTo(70);
        assertThat(progressService.fallback(SpeakingRecordStatus.COMPLETED)).isEqualTo(100);
        assertThat(progressService.fallback(SpeakingRecordStatus.FAILED)).isEqualTo(100);
    }

    private SpeakingRecordEntity record(Long id, SpeakingRecordStatus status) {
        SpeakingRecordEntity record = SpeakingRecordEntity.createPending(
                2L,
                null,
                "object.webm",
                "answer",
                "audio/webm",
                100L,
                10
        );
        ReflectionTestUtils.setField(record, "id", id);
        ReflectionTestUtils.setField(record, "status", status);
        return record;
    }
}
