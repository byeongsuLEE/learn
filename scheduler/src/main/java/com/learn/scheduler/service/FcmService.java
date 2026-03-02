package com.learn.scheduler.service;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FcmService {

    /**
     * Firebase SDK의 sendAsync를 직접 사용하여 비동기로 발송합니다.
     * ApiFuture를 CompletableFuture로 변환하여 반환합니다.
     */
    public CompletableFuture<String> sendNotificationAsync(String token, String title, String body,
            boolean validateOnly) {
        log.info("Sending FCM notification via SDK sendAsync. Token: {}, validateOnly: {}", token, validateOnly);

        Message message = createMessage(token, title, body);

        // Firebase Admin SDK의 sendAsync는 ApiFuture를 반환합니다.
        ApiFuture<String> future = FirebaseMessaging.getInstance().sendAsync(message, validateOnly);

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        // ApiFuture를 CompletableFuture로 변환하기 위해 콜백을 추가합니다.
        ApiFutures.addCallback(future, new ApiFutureCallback<String>() {
            @Override
            public void onFailure(Throwable t) {
                log.error("FCM SDK sendAsync failed", t);
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

    /**
     * Spring의 @Async를 사용하여 메소드 자체를 비동기로 실행합니다. (레거시 방식 재현용)
     */
    @Async
    public CompletableFuture<Void> sendNotificationWithSpringAsync(String token, String title, String body,
            boolean validateOnly) {
        log.info("Sending FCM notification via Spring @Async. Token: {}, validateOnly: {}", token, validateOnly);

        Message message = createMessage(token, title, body);

        try {
            // 내부에서는 동기 방식인 send()를 호출하지만, 메소드 자체가 @Async에 의해 별도 스레드에서 돕니다.
            String response = FirebaseMessaging.getInstance().send(message, validateOnly);
            log.info("FCM Spring @Async success. Response: {}", response);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("FCM Spring @Async failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private Message createMessage(String token, String title, String body) {
        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
    }
}
