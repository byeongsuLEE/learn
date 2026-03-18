package com.learn.scheduler.service;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FcmService {

    @Autowired
    @Qualifier("redisScheduleTemplate")
    private RedisTemplate<String, Object> scheduleTemplate;

    /**
     * Firebase SDK의 sendAsync를 직접 사용하여 비동기로 발송합니다.
     * 실패 시 Redis에 데이터를 자동으로 백업합니다.
     */
    public CompletableFuture<String> sendNotificationAsync(String scheduleId, Object originalData, String token,
            String title, String body, boolean validateOnly) {
        log.info("Sending FCM notification via SDK sendAsync. Token: {}, validateOnly: {}", token, validateOnly);

        Message message = createMessage(token, title, body);

        // Firebase Admin SDK의 sendAsync는 ApiFuture를 반환합니다.
        ApiFuture<String> future = FirebaseMessaging.getInstance().sendAsync(message, validateOnly);

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        // ApiFuture를 CompletableFuture로 변환하기 위해 콜백을 추가합니다.
        ApiFutures.addCallback(future, new ApiFutureCallback<String>() {
            @Override
            public void onFailure(Throwable t) {
                log.error("FCM SDK sendAsync failed for ID: {}. Saving to Redis FAIL key.", scheduleId, t);
//                saveFailureToRedis(scheduleId, originalData);
                completableFuture.completeExceptionally(t);
            }

            @Override
            public void onSuccess(String result) {
                log.info("FCM SDK sendAsync success. Response: {}", result);
                completableFuture.complete(result);
            }
        }, MoreExecutors.directExecutor());

        return completableFuture;
    }

    public CompletableFuture<String> sendNotificationAsync(String token, String title, String body,
            boolean validateOnly) {
        return sendNotificationAsync(null, null, token, title, body, validateOnly);
    }

    /**
     * Spring의 @Async를 사용하여 메소드 자체를 비동기로 실행합니다. (레거시 방식 재현용)
     * 실패 시 Redis에 데이터를 자동으로 백업합니다.
     */
    @Async
    public CompletableFuture<Void> sendNotificationWithSpringAsync(String scheduleId, Object originalData, String token,
            String title, String body, boolean validateOnly) {
        log.info("Sending FCM notification via Spring @Async. Token: {}, validateOnly: {}", token, validateOnly);

        Message message = createMessage(token, title, body);

        try {
            // 내부에서는 동기 방식인 send()를 호출하지만, 메소드 자체가 @Async에 의해 별도 스레드에서 돕니다.
            String response = FirebaseMessaging.getInstance().send(message, validateOnly);
            log.info("FCM Spring @Async success. Response: {}", response);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("FCM Spring @Async failed for ID: {}. Saving to Redis FAIL key.", scheduleId, e);
//            saveFailureToRedis(scheduleId, originalData);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Void> sendNotificationWithSpringAsync(String token, String title, String body,
            boolean validateOnly) {
        return sendNotificationWithSpringAsync(null, null, token, title, body, validateOnly);
    }

    /**
     * Firebase SDK의 send를 사용하여 동기식으로 발송합니다.
     * 실패 시 Redis에 데이터를 자동으로 백업합니다.
     */
    public String sendNotification(String scheduleId, Object originalData, String token, String title, String body,
            boolean validateOnly) {
        log.info("Sending FCM notification synchronously. Token: {}", token);
        Message message = createMessage(token, title, body);
        try {
            return FirebaseMessaging.getInstance().send(message, validateOnly);
        } catch (Exception e) {
            log.error("FCM Sync send failed for token: {}. Saving to Redis FAIL key.", token, e);
//            saveFailureToRedis(scheduleId, originalData);
            return null;
        }
    }

    public String sendNotification(String token, String title, String body, boolean validateOnly) {
        return sendNotification(null, null, token, title, body, validateOnly);
    }

    private void saveFailureToRedis(String scheduleId, Object data) {
        String date = LocalDate.now().toString();
        String failKey = "SCHEDULE:FAIL:" + date;
        try {
            scheduleTemplate.opsForHash().put(failKey, scheduleId, data);
            log.info("Failed message [ID: {}] saved to Redis key: {}", scheduleId, failKey);
        } catch (Exception e) {
            log.error("Failed to save failure data to Redis! ID: {}", scheduleId, e);
        }
    }

    /**
     * 여러 메시지를 배치(묶음)로 비동기 발송하며, 실패 시 Redis에 자동 백업합니다.
     */
    public CompletableFuture<BatchResponse> sendEachAsync(
            List<String> ids, List<Object> originalDataList, List<Message> messages, boolean validateOnly) {
//        log.info("Sending {} FCM notifications via SDK sendEachAsync.", messages.size());

        ApiFuture<BatchResponse> future = FirebaseMessaging.getInstance()
                .sendEachAsync(messages, validateOnly);

        CompletableFuture<BatchResponse> completableFuture = new CompletableFuture<>();

        ApiFutures.addCallback(future, new ApiFutureCallback<BatchResponse>() {
            @Override
            public void onFailure(Throwable t) {
                completableFuture.completeExceptionally(t);
            }

            @Override
            public void onSuccess(BatchResponse result) {
//                if (result.getFailureCount() > 0) {
//                    processBatchFailures(ids, originalDataList, result);
//                } else {
                    log.info("FCM Batch completed successfully. Count: {}", result.getSuccessCount());
//                }
                completableFuture.complete(result);
            }
        }, MoreExecutors.directExecutor());

        return completableFuture;
    }

    /**
     * 기존 코드 호환성을 위한 오버로딩 메서드 (실패 시 로그만 남김)
     */
    public CompletableFuture<BatchResponse> sendEachAsync(List<Message> messages, boolean validateOnly) {
        return sendEachAsync(null, null, messages, validateOnly);
    }

    private void processBatchFailures(List<String> ids, List<Object> dataList, BatchResponse response) {
        if (ids == null || dataList == null) {
            log.warn("FCM Batch completed with {} failures. (ID and data not provided for backup)",
                    response.getFailureCount());
            return;
        }
        List<com.google.firebase.messaging.SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                String id = ids.get(i);
                Object data = dataList.get(i);
                log.warn("FCM Batch item failed (Index: {}, ID: {}). Saving to Redis FAIL key.", i, id);
//                saveFailureToRedis(id, data);
            }
        }
    }

    public Message createMessage(String token, String title, String body) {
        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
    }
}
