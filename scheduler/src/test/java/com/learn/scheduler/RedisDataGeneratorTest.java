package com.learn.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.scheduler.dto.CreateScheduleInputDto;
import com.learn.scheduler.service.FcmService;
import com.learn.scheduler.service.RedisDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootTest
class RedisDataGeneratorTest {

    @Autowired
    private RedisDataGenerator redisDataGenerator;

    @Autowired
    private FcmService fcmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("redisUserTemplate")
    private RedisTemplate<String, String> userTemplate;

    @Autowired
    @Qualifier("redisScheduleTemplate")
    private RedisTemplate<String, Object> scheduleTemplate;

    private static final String TEST_TOKEN = "fV9xsBrIDF64Vyd1eHbVcF:APA91bHKvSqPZyJSsAXWz7IX_WsM8PnrUZ1cZRpKUKNR6EYo0mDgj55IDaIJJVH29csjc72QSw-CFw-9crLiaZ4ao9J4PanqEC71CEOIopX02ASEiC8XUO4";

    private int getDbIndex(RedisTemplate<?, ?> template) {
        try {
            RedisConnectionFactory factory = template.getConnectionFactory();
            if (factory instanceof LettuceConnectionFactory) {
                return ((LettuceConnectionFactory) factory).getDatabase();
            }
        } catch (Exception e) {
            log.warn("Could not determine DB index", e);
        }
        return -1;
    }

    @Test
    void generateData() {
        log.info("Running generateData...");
        redisDataGenerator.saveTestUser(1L, TEST_TOKEN);
        redisDataGenerator.generate1000SchedulesForToday();
    }

    @Test
    void FCM_sendAsync_비동기_전송() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;
        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);

        if (entries.isEmpty()) {
            log.warn("No schedules found for today. Please run generateData first.");
            return;
        }

        log.info("Starting async FCM broadcast for {} schedules", entries.size());

        List<CompletableFuture<String>> futures = entries.values().stream()
                .map(scheduleInfo -> objectMapper.convertValue(scheduleInfo, CreateScheduleInputDto.class))
                .map(dto -> fcmService.sendNotificationAsync(
                        TEST_TOKEN,
                        dto.getTitle() != null ? dto.getTitle() : "Scheduled Notification",
                        dto.getContent(),
                        true))
                .toList();

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0]))
                .join();

        log.info("All async FCM notifications have been processed.");
    }

    /**
     * 데이터 생성부터 검증까지 한 번에 수행하는 테스트입니다. (JUnit의 실행 순서 의존성 해결)
     * This test performs both data generation and verification in one go to avoid
     * JUnit execution order issues.
     */
    @Test
    void FCM_send_java_async_알림() {
        // 1. 데이터 생성 (필요 시)
        // redisDataGenerator.generate1000SchedulesForToday();

        // 2. 생성된 데이터 순회 및 검증 (Iterate and verify)
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;

        log.info("Step 2: Starting legacy logic comparison for key: {} (DB {})", scheduleKey,
                getDbIndex(scheduleTemplate));

        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);
        log.info("Total entries found in DB for key {}: {}", scheduleKey, entries.size());

        if (entries.isEmpty()) {
            throw new RuntimeException("Data was generated but not found in Redis! Check if DB index is correct.");
        }

        List<CompletableFuture<Void>> futures = entries.values().stream()
                .map(scheduleInfo -> objectMapper.convertValue(scheduleInfo, CreateScheduleInputDto.class))
                .map(dto -> {
                    log.info("Sending notification via Spring Async: {}", dto.getContent());
                    return fcmService.sendNotificationWithSpringAsync(
                            TEST_TOKEN,
                            dto.getType() != null ? dto.getType() : "Notification",
                            dto.getContent(),
                            true // dryRun
                    );
                })
                .toList();

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Finished all async notifications comparison test.");
    }
}
