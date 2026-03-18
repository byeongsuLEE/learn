package com.learn.scheduler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.SendResponse;
import com.learn.scheduler.dto.CreateScheduleInputDto;
import com.learn.scheduler.service.FcmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.*;

@Configuration
@Slf4j
public class FcmBatchConfig {

    private final JobRepository jobRepository;
    private final FcmService fcmService;
    private final RedisTemplate<String, Object> scheduleTemplate;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;

    public FcmBatchConfig(
            JobRepository jobRepository,
            FcmService fcmService,
            @Qualifier("redisScheduleTemplate") RedisTemplate<String, Object> scheduleTemplate,
            PlatformTransactionManager transactionManager,
            ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.fcmService = fcmService;
        this.scheduleTemplate = scheduleTemplate;
        this.transactionManager = transactionManager;
        this.objectMapper = objectMapper;
    }

    private static final String TEST_TOKEN = "fV9xsBrIDF64Vyd1eHbVcF:APA91bHKvSqPZyJSsAXWz7IX_WsM8PnrUZ1cZRpKUKNR6EYo0mDgj55IDaIJJVH29csjc72QSw-CFw-9crLiaZ4ao9J4PanqEC71CEOIopX02ASEiC8XUO4";

    @Bean
    public Job fcmTestJob() {
        return new JobBuilder("fcmTestJob", jobRepository)
                .start(fcmTestStep())
                .build();
    }

    @Bean
    public Job fcmRetryJob() {
        return new JobBuilder("fcmRetryJob", jobRepository)
                .start(fcmRetryStep())
                .build();
    }

    private Step fcmTestStep() {
        return new StepBuilder("fcmTestStep",
                jobRepository).<Map.Entry<Object, Object>, Map.Entry<Object, Object>>chunk(500, transactionManager)
                .reader(fcmHashReader())
                .writer(fcmTestFailWriter())
                .build();
    }

    @Bean
    public Job fcmNotificationJob() {
        return new JobBuilder("fcmNotification", jobRepository)
                .start(fcmStep())
                .build();
    }

    @Bean
    public Step fcmStep() {
        return new StepBuilder("fcmStep", jobRepository).<Map.Entry<Object, Object>, Map.Entry<Object, Object>>chunk(
                500, transactionManager)
                .reader(fcmTestReader())
                .writer(fcmHashWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Map.Entry<Object, Object>> fcmHashReader() {
        return new ItemReader<>() {
            private Cursor<Map.Entry<Object, Object>> cursor;

            @Override
            public Map.Entry<Object, Object> read() {
                if (cursor == null) {
                    String scheduleKey = "SCHEDULE:" + LocalDate.now();
                    cursor = scheduleTemplate.opsForHash().scan(scheduleKey,
                            ScanOptions.scanOptions().count(500).build());
                }
                if (cursor != null && cursor.hasNext()) {
                    Map.Entry<Object, Object> entry = cursor.next();
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue());
                }
                return null;
            }
        };
    }

    @Bean
    public ItemReader<Map.Entry<Object, Object>> fcmTestReader() {
        return new ItemReader<>() {
            private Cursor<Map.Entry<Object, Object>> cursor;
            private int count = 0;

            @Override
            public Map.Entry<Object, Object> read() {
                if (count >= 500)
                    return null;
                if (cursor == null) {
                    String scheduleKey = "SCHEDULE:" + LocalDate.now();
                    cursor = scheduleTemplate.opsForHash().scan(scheduleKey,
                            ScanOptions.scanOptions().count(500).build());
                }
                if (cursor.hasNext()) {
                    count++;
                    return cursor.next();
                }
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<Map.Entry<Object, Object>> fcmHashWriter() {
        return chunk -> {
            List<String> coupleCode = new ArrayList<>();
            List<Object> scheduleDatas = new ArrayList<>();
            List<Message> messages = new ArrayList<>();
            String testToken = TEST_TOKEN;

            for (Map.Entry<Object, Object> entry : chunk.getItems()) {
                coupleCode.add(entry.getKey().toString());
                Object scheduleData = entry.getValue();
                scheduleDatas.add(scheduleData);

                CreateScheduleInputDto dto = objectMapper.convertValue(scheduleData, CreateScheduleInputDto.class);
                messages.add(fcmService.createMessage(testToken, dto.getTitle(), dto.getContent()));
            }

            fcmService.sendEachAsync(coupleCode, scheduleDatas, messages, true);
            log.info("Batch 전송 요청 완료: {}건", messages.size());
        };
    }

    @Bean
    public ItemWriter<Map.Entry<Object, Object>> fcmTestFailWriter() {
        return chunk -> {
            List<String> coupleCode = new ArrayList<>();
            List<String> tokens = new ArrayList<>();
            List<Object> scheduleDatas = new ArrayList<>();
            List<Message> messages = new ArrayList<>();
            String testToken = TEST_TOKEN;
            String failedKey = "SCHEDULE:FAIL:" + LocalDate.now();

            for (Map.Entry<Object, Object> entry : chunk.getItems()) {
                coupleCode.add(entry.getKey().toString());
                tokens.add(testToken);
                Object scheduleData = entry.getValue();
                scheduleDatas.add(scheduleData);

                CreateScheduleInputDto dto = objectMapper.convertValue(scheduleData, CreateScheduleInputDto.class);
                messages.add(fcmService.createMessage(testToken, dto.getTitle(), dto.getContent()));
            }

            fcmService.sendEachAsync(coupleCode, scheduleDatas, messages, true)
                    .thenAccept(batchResponse -> {
                        if (batchResponse == null)
                            return;

                        List<String> failedJsonList = new ArrayList<>();
                        List<SendResponse> responses = batchResponse.getResponses();

                        for (int i = 0; i < responses.size(); i++) {
                            if (!responses.get(i).isSuccessful()) {
                                String errorCode = responses.get(i).getException().getMessagingErrorCode().name();
                                if ("UNAVAILABLE".equals(errorCode) || "INTERNAL".equals(errorCode)) {
                                    CreateScheduleInputDto dto = objectMapper.convertValue(scheduleDatas.get(i),
                                            CreateScheduleInputDto.class);

                                    Map<String, Object> failInfo = new HashMap<>();
                                    failInfo.put("token", tokens.get(i));
                                    failInfo.put("retryCount", 1);
                                    failInfo.put("title", dto.getTitle());
                                    failInfo.put("content", dto.getContent());

                                    try {
                                        failedJsonList.add(objectMapper.writeValueAsString(failInfo));
                                    } catch (Exception e) {
                                        log.error(">>> JSON 직렬화 실패", e);
                                    }
                                }
                            }
                        }

                        if (!failedJsonList.isEmpty()) {
                            scheduleTemplate.opsForSet().add(failedKey, failedJsonList.toArray());
                            log.warn(">>> [실패] 재시도 대상(JSON) {}건 저장 완료", failedJsonList.size());
                        }

                        log.info(">>> 전송 완료 - 성공: {}건, 실패: {}건",
                                batchResponse.getSuccessCount(),
                                batchResponse.getFailureCount());
                    });
        };
    }

    @Bean
    public Step fcmRetryStep() {
        return new StepBuilder("fcmRetryStep", jobRepository)
                .<String, String>chunk(500, transactionManager)
                .reader(fcmRetryReader())
                .writer(fcmRetryWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> fcmRetryReader() {
        return () -> {
            Object popped = scheduleTemplate.opsForSet().pop("SCHEDULE:FAIL:" + LocalDate.now());
            return popped != null ? popped.toString() : null;
        };
    }

    @Bean
    public ItemWriter<String> fcmRetryWriter() {
        return chunk -> {
            List<? extends String> rawItems = chunk.getItems();
            List<String> tokens = new ArrayList<>();
            List<Integer> retryCounts = new ArrayList<>();
            List<Object> scheduleDatas = new ArrayList<>();
            List<Message> messages = new ArrayList<>();

            String failedKey = "SCHEDULE:FAIL:" + LocalDate.now();

            for (String json : rawItems) {
                try {
                    Map<String, Object> failInfo = objectMapper.readValue(json, Map.class);
                    String token = (String) failInfo.get("token");
                    int count = ((Number) failInfo.get("retryCount")).intValue();
                    String title = (String) failInfo.get("title");
                    String content = (String) failInfo.get("content");

                    tokens.add(token);
                    retryCounts.add(count);

                    Map<String, String> data = new HashMap<>();
                    data.put("title", title);
                    data.put("content", content);
                    scheduleDatas.add(data);

                    messages.add(fcmService.createMessage(token, "[재시도 " + count + "회차] " + title, content));
                } catch (Exception e) {
                    log.error(">>> JSON 파싱 실패: {}", json, e);
                }
            }

            if (messages.isEmpty())
                return;

            fcmService.sendEachAsync(tokens, scheduleDatas, messages, true)
                    .thenAccept(batchResponse -> {
                        if (batchResponse == null)
                            return;

                        List<SendResponse> responses = batchResponse.getResponses();
                        List<String> nextRetries = new ArrayList<>();

                        for (int i = 0; i < responses.size(); i++) {
                            String token = tokens.get(i);
                            if (!responses.get(i).isSuccessful()) {
                                String errorCode = responses.get(i).getException().getMessagingErrorCode().name();
                                int currentCount = retryCounts.get(i);
                                if (currentCount < 3
                                        && ("UNAVAILABLE".equals(errorCode) || "INTERNAL".equals(errorCode))) {
                                    Map<String, Object> nextInfo = new HashMap<>();
                                    nextInfo.put("token", token);
                                    nextInfo.put("retryCount", currentCount + 1);

                                    Map<String, String> originalData = (Map<String, String>) scheduleDatas.get(i);
                                    nextInfo.put("title", originalData.get("title"));
                                    nextInfo.put("content", originalData.get("content"));

                                    try {
                                        nextRetries.add(objectMapper.writeValueAsString(nextInfo));
                                    } catch (Exception e) {
                                        log.error(">>> JSON 직렬화 실패", e);
                                    }
                                } else {
                                    log.error(">>> [재시도 포기] Token: {}, Error: {}, Count: {}", token, errorCode,
                                            currentCount);
                                }
                            }
                        }

                        if (!nextRetries.isEmpty()) {
                            scheduleTemplate.opsForSet().add(failedKey, nextRetries.toArray());
                            log.warn(">>> [재시도] 차기 재시도 대상 {}건 등록 완료", nextRetries.size());
                        }

                        log.info(">>> [재시도 결과] 성공: {}건, 실패: {}건",
                                batchResponse.getSuccessCount(), batchResponse.getFailureCount());
                    });
        };
    }
}
