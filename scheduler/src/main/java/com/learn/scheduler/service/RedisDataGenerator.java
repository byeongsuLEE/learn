package com.learn.scheduler.service;

import com.learn.scheduler.dto.CreateScheduleInputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class RedisDataGenerator {

    private final RedisTemplate<String, String> userTemplate;
    private final RedisTemplate<String, Object> scheduleTemplate;

    public RedisDataGenerator(
            @Qualifier("redisUserTemplate") RedisTemplate<String, String> userTemplate,
            @Qualifier("redisScheduleTemplate") RedisTemplate<String, Object> scheduleTemplate) {
        this.userTemplate = userTemplate;
        this.scheduleTemplate = scheduleTemplate;

        log.info("RedisDataGenerator initialized.");
        log.info("userTemplate DB: {}", getDbIndex(userTemplate));
        log.info("scheduleTemplate DB: {}", getDbIndex(scheduleTemplate));
    }

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

    public void saveTestUser(Long userId, String fcmToken) {
        String key = "USER:" + userId;
        userTemplate.opsForValue().set(key, fcmToken);
        log.info("Saved test user token to Redis (DB {}). Key: {}, Token: {}", getDbIndex(userTemplate), key, fcmToken);
    }

    public void generate1000SchedulesForToday() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = now.format(formatter);
        String key = "SCHEDULE:" + dateStr;

        log.info("Starting generation of 1000 schedules for today ({}) in Redis (DB {})...", dateStr,
                getDbIndex(scheduleTemplate));

        for (int i = 0; i < 1000; i++) {
            String field = "COUPLE_" + i;

            CreateScheduleInputDto dto = CreateScheduleInputDto.builder()
                    .title("Today's Schedule " + i)
                    .content("Content for " + field)
                    .productId((long) (Math.random() * 1000))
                    .type(i % 2 == 0 ? "WEDDING_HALL" : "STUDIO")
                    .build();

            scheduleTemplate.opsForHash().put(key, field, dto);
        }

        log.info("Completed generation of 1000 schedules. Current size for mapping {}: {}", key,
                scheduleTemplate.opsForHash().size(key));
    }

    public void generate100000SchedulesForToday() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = now.format(formatter);
        String key = "SCHEDULE:" + dateStr;

        log.info("Starting generation of 100,000 schedules for today ({}) in Redis (DB {})...", dateStr,
                getDbIndex(scheduleTemplate));

        int total = 100000;
        int batchSize = 1000;

        for (int i = 0; i < total; i += batchSize) {
            final int start = i;
            final int end = Math.min(i + batchSize, total);

            scheduleTemplate
                    .executePipelined((org.springframework.data.redis.core.RedisCallback<Object>) connection -> {
                        for (int j = start; j < end; j++) {
                            String field = "COUPLE_" + j;
                            CreateScheduleInputDto dto = CreateScheduleInputDto.builder()
                                    .title("100k Schedule " + j)
                                    .content("Content for " + field)
                                    .productId((long) (Math.random() * 100000))
                                    .type(j % 2 == 0 ? "WEDDING_HALL" : "STUDIO")
                                    .build();

                            byte[] keyBytes = scheduleTemplate.getStringSerializer().serialize(key);
                            byte[] fieldBytes = scheduleTemplate.getStringSerializer().serialize(field);
                            byte[] valueBytes = ((org.springframework.data.redis.serializer.RedisSerializer<Object>) scheduleTemplate
                                    .getValueSerializer()).serialize(dto);

                            connection.hashCommands().hSet(keyBytes, fieldBytes, valueBytes);
                        }
                        return null;
                    });

            if (end % 10000 == 0) {
                log.info("Progress: {}/{} schedules generated...", end, total);
            }
        }

        log.info("Completed generation of 100,000 schedules. Key: {}", key);
    }



}
