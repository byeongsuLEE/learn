package com.lbs.speaking.speaking.worker;

import com.lbs.speaking.config.SpeakingProperties;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import com.lbs.speaking.speaking.infrastructure.repository.AnalysisJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.SpeakingRecordJpaRepository;
import com.lbs.speaking.speaking.service.AudioStorageService;
import com.lbs.speaking.speaking.service.RedisLockService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpeakingJobs {

    private final SpeakingProperties properties;
    private final RedisLockService redisLockService;
    private final SpeakingRecordJpaRepository recordRepository;
    private final AnalysisJpaRepository analysisRepository;
    private final AnalysisPublisher analysisPublisher;
    private final AudioStorageService audioStorageService;

    @Scheduled(cron = "${speaking.jobs.recovery-cron}")
    public void recoverStaleRecords() {
        String lockKey = "speaking:lock:recovery-job";
        if (!redisLockService.tryLock(lockKey, Duration.ofMinutes(4))) {
            return;
        }
        try {
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(properties.jobs().staleAnalysisMinutes());
            recordRepository.findTop100ByStatusInAndUpdatedAtBeforeAndDeletedAtIsNullOrderByUpdatedAtAsc(
                            List.of(SpeakingRecordStatus.PENDING_ANALYSIS, SpeakingRecordStatus.ANALYZING),
                            threshold
                    )
                    .stream()
                    .filter(record -> !analysisRepository.existsByRecordId(record.getId()))
                    .forEach(record -> analysisPublisher.publish(new AnalysisRequestedEvent(record.getId(), record.getUserId(), 1)));
        } finally {
            redisLockService.unlock(lockKey);
        }
    }

    @Scheduled(cron = "${speaking.jobs.cleanup-cron}")
    public void cleanupTempObjects() {
        String lockKey = "speaking:lock:temp-cleanup-job";
        if (!redisLockService.tryLock(lockKey, Duration.ofHours(1))) {
            return;
        }
        try {
            Instant threshold = Instant.now().minus(Duration.ofHours(properties.jobs().tempCleanupHours()));
            audioStorageService.findTempObjectsOlderThan(threshold)
                    .forEach(audioStorageService::delete);
        } catch (Exception exception) {
            log.warn("Temp object cleanup failed", exception);
        } finally {
            redisLockService.unlock(lockKey);
        }
    }
}
