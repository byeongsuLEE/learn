package com.learn.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.Message;
import com.learn.scheduler.dto.CreateScheduleInputDto;
import com.learn.scheduler.service.FcmService;
import com.learn.scheduler.service.RedisDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fcmNotificationJob;

    @Autowired
    private Job fcmRetryJob;

    @Autowired
    private Job fcmTestJob;

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
    void FCM_10만개_일괄_전송_메모리_오류_테스트() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;

        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);
        log.info("{} 건의 알림을 한 번에 비동기 전송 시도", entries.size());

        try {
            entries.entrySet().stream()
                    .map(entry -> {
                        String id = entry.getKey().toString();
                        Object rawData = entry.getValue();
                        CreateScheduleInputDto dto = objectMapper.convertValue(rawData, CreateScheduleInputDto.class);
                        return fcmService.sendNotificationAsync(id, rawData, TEST_TOKEN, dto.getTitle(),
                                dto.getContent(), true);
                    })
                    .toList();

            log.info("오류 없이 10만 건 비동기 발송 요청 완료 (실제 전송 완료는 아님)");
        } catch (Exception e) {
            log.error("10만 건 비동기 전송 중 메모리 또는 리소스 오류 발생!", e);
            throw e;
        }
    }

    @Test
    void 데이터_생성_테스트() {
        log.info("데이터 생성 테스트 실행 중...");
        redisDataGenerator.saveTestUser(1L, TEST_TOKEN);
        redisDataGenerator.generate1000SchedulesForToday();
    }

    @Test
    void 대량_데이터_10만개_생성_테스트() {
        log.info("부하 테스트를 위한 10만 개 일정 생성 중...");
        redisDataGenerator.generate100000SchedulesForToday();
    }

    @Test
    void 힙_메모리_부하_테스트_4천만건() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;

        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);
        Collection<Object> values = entries.values();
        log.info("원본 데이터 로드 완료. 크기: {}", values.size());

        List<Object> memoryBomb = new ArrayList<>();
        long totalCount = 0;

        try {
            for (int i = 1; i <= 5000; i++) {
                memoryBomb.addAll(values);
                totalCount += values.size();
                log.info("통과 {}: 리스트 내 총 객체 수 = {}", i, totalCount);
            }
        } catch (OutOfMemoryError e) {
            log.error("💥💥💥 BOOM! 메모리 고갈 💥💥💥");
            log.error("리스트 내 최종 객체 수: {}", totalCount);
            throw e;
        }
    }

    @Test
    // 3초 걸리지만, 잘 됐는지 확인이 안됨

    void FCM_Spring_Async_비동기_알림_테스트() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;

        log.info("Spring Async 방식 비교 테스트 시작 (Key: {}, DB: {})", scheduleKey,
                getDbIndex(scheduleTemplate));

        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);
        log.info("DB에서 발견된 총 엔트리: {}", entries.size());

        if (entries.isEmpty()) {
            throw new RuntimeException("데이터가 Redis에 없습니다! DB 인덱스를 확인하세요.");
        }

        List<CompletableFuture<Void>> futures = entries.entrySet().stream()
                .map(entry -> {
                    String id = entry.getKey().toString();
                    Object rawData = entry.getValue();
                    CreateScheduleInputDto dto = objectMapper.convertValue(rawData, CreateScheduleInputDto.class);

                    log.info("Spring Async로 알림 발송 중 (ID: {}): {}", id, dto.getContent());
                    return fcmService.sendNotificationWithSpringAsync(
                            id,
                            rawData,
                            TEST_TOKEN,
                            dto.getType() != null ? dto.getType() : "Notification",
                            dto.getContent(),
                            true // dryRun
                    );
                })
                .toList();

        // CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("모든 Spring Async 비동기 알림 비교 테스트 완료.");
    }

    @Test
    void FCM_10만개_배치_전송_테스트_List기반() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;
        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);

        log.info("{} 건 알림에 대한 배치 처리(List 기반) 시작...", entries.size());

        List<Object> values = new java.util.ArrayList<>(entries.values());
        int total = values.size();
        int batchSize = 500;

        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            List<Object> batch = values.subList(i, end);

            log.info("배치 {} ~ {} 처리 중...", i, end);

            List<CompletableFuture<String>> futures = batch.stream()
                    .map(info -> objectMapper.convertValue(info, CreateScheduleInputDto.class))
                    .map(dto -> fcmService.sendNotificationAsync(TEST_TOKEN, dto.getTitle(), dto.getContent(), true))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        log.info("10만 건 알림에 대한 모든 배치 처리(List 기반) 완료.");
    }
    // 5초 걸림

    // 전체적인 시간 : 1분 30초
    @Test
    void FCM_비동기_전송_테스트() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;
        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);

        if (entries.isEmpty()) {
            log.warn("오늘자 일정이 없습니다. 데이터 생성 테스트를 먼저 실행하세요.");
            return;
        }

        log.info("{} 건의 일정에 대해 비동기 FCM 발송 시작", entries.size());

        List<CompletableFuture<String>> futures = entries.entrySet().stream()
                .map(entry -> {
                    String id = entry.getKey().toString();
                    Object rawData = entry.getValue();
                    CreateScheduleInputDto dto = objectMapper.convertValue(rawData, CreateScheduleInputDto.class);

                    return fcmService.sendNotificationAsync(
                            id,
                            rawData,
                            TEST_TOKEN,
                            dto.getTitle() != null ? dto.getTitle() : "Scheduled Notification",
                            dto.getContent(),
                            true);
                })
                .toList();

        CompletableFuture.allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0]))
                .join();

        log.info("모든 비동기 FCM 알림 처리가 완료되었습니다.");
    }

    /**
     * hScan과 sendEachAsync를 사용하여 메모리와 네트워크를 모두 최적화한 10만 건 배치 전송 테스트
     */
    // 6초걸림
    @Test
    void FCM_10만개_배치_전송_테스트() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;

        log.info("hScan + sendEachAsync 기반 비차단 배치 전송 시작 (Key: {})", scheduleKey);
        long startTime = System.currentTimeMillis();

        int batchSize = 500;
        List<String> currentBatchIds = new ArrayList<>();
        List<Object> currentBatchData = new ArrayList<>();
        List<Message> currentBatchMessages = new ArrayList<>();
        List<CompletableFuture<BatchResponse>> batchFutures = new ArrayList<>();
        long totalProcessed = 0;

        try (var cursor = scheduleTemplate.opsForHash().scan(scheduleKey,
                ScanOptions.scanOptions().count(batchSize).build())) {

            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();
                String scheduleId = entry.getKey().toString();
                Object rawData = entry.getValue();
                CreateScheduleInputDto dto = objectMapper.convertValue(rawData, CreateScheduleInputDto.class);

                currentBatchIds.add(scheduleId);
                currentBatchData.add(rawData);
                currentBatchMessages.add(fcmService.createMessage(TEST_TOKEN, dto.getTitle(), dto.getContent()));
                totalProcessed++;

                if (currentBatchMessages.size() >= batchSize) {
                    batchFutures.add(fcmService.sendEachAsync(
                            new ArrayList<>(currentBatchIds),
                            new ArrayList<>(currentBatchData),
                            new ArrayList<>(currentBatchMessages),
                            true));
                    currentBatchIds.clear();
                    currentBatchData.clear();
                    currentBatchMessages.clear();

                    if (totalProcessed % 10000 == 0) {
                        log.info("현재까지 스캔 및 전송 요청 완료: {}", totalProcessed);
                    }
                }
            }
        } catch (Exception e) {
            log.error("hScan 또는 배치 전송 중 오류 발생", e);
            throw new RuntimeException(e);
        }

        if (!currentBatchMessages.isEmpty()) {
            batchFutures.add(fcmService.sendEachAsync(currentBatchIds, currentBatchData, currentBatchMessages, true));
        }

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;

        log.info("================================================");
        log.info("🚀 최적화 배치 전송 완료 (비차단)");
        log.info("📊 총 처리 건수: {} 건", totalProcessed);
        log.info("⏱️ 총 소요 시간: {} 초", durationSeconds);
        log.info("⏱️ 건당 평균 속도: {} ms", totalProcessed > 0 ? (endTime - startTime) / (double) totalProcessed : 0);
    }

    @Test
    void FCM_배치_에러_복구_재시도_테스트() {
        LocalDate localDate = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + localDate;

        int testCount = 50;
        List<String> ids = new ArrayList<>();
        List<Object> dataList = new ArrayList<>();
        List<Message> messageBatch = new ArrayList<>();

        Map<Object, Object> entries = scheduleTemplate.opsForHash().entries(scheduleKey);
        int count = 0;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            if (count >= testCount)
                break;
            String id = entry.getKey().toString();
            Object rawData = entry.getValue();
            CreateScheduleInputDto dto = objectMapper.convertValue(rawData, CreateScheduleInputDto.class);

            ids.add(id);
            dataList.add(rawData);
            String token = (count % 5 == 0) ? "INVALID_TOKEN_FOR_RETRY_TEST" : TEST_TOKEN;
            messageBatch.add(fcmService.createMessage(token, "[재시도테스트] " + dto.getTitle(), dto.getContent()));
            count++;
        }

        log.info("FCM 배치 에러 복구 테스트 시작 (대상: {}건)", messageBatch.size());
        fcmService.sendEachAsync(ids, dataList, messageBatch, true).join();
        log.info("테스트 종료. Redis fail 키를 확인하세요.");
    }

    @Test
    void FCM_배치_500개테스트() throws Exception {
        JobParameters parameter = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        log.info("Test : 시작");
        JobExecution execution = jobLauncher.run(fcmNotificationJob, parameter);
        // 3. 결과 확인
        log.info("### Job 실행 상태: {}", execution.getStatus());
        log.info("### Job 종료 코드: {}", execution.getExitStatus().getExitCode());

        // 정상적으로 완료되었는지 검증
        if ("COMPLETED".equals(execution.getExitStatus().getExitCode())) {
            log.info("### 테스트 성공: 한 사이클이 정상적으로 처리되었습니다.");
        } else {
            log.error("### 테스트 실패: 에러 로그를 확인하세요.");
        }
        // dddddsfdsfsd
    }

    @Test
    void Redis_데이터_존재_확인() {
        String key = "SCHEDULE:2026-03-15";
        Long size = scheduleTemplate.opsForHash().size(key);
        log.info("### Redis Key: {}, 데이터 개수: {}", key, size);
    }

    // 35초 걸림 = join x
    // join o 할때 1분 56초걸림
    @Test
    void FCM_BATCH_실패_TEST() throws Exception {
        // 1. 테스트 데이터 생성은 생략 (사용자 요청)

        JobParameters parameter = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("testMode", "failure")
                .toJobParameters();

        log.info("### 비동기 FCM 배치 실패 테스트 시작");
        JobExecution execution = jobLauncher.run(fcmTestJob, parameter);
        log.info("### Job 실행 상태: {}", execution.getStatus());

        String failedKey = "SCHEDULE:FAIL:" + LocalDate.now();
        Long failCount = scheduleTemplate.opsForSet().size(failedKey);
        log.info("### Redis 실패 기록 확인 키: {}", failedKey);
        log.info("### 등록된 실패 건수: {}", failCount);
    }

    @Test
    void FCM_재시도_단독_테스트() throws Exception {
        LocalDate today = LocalDate.now();
        String failedKey = "SCHEDULE:FAIL:" + today;
        String failDataKey = "SCHEDULE:FAIL_DATA:" + today;

        log.info("### [시작] 재시도 단계 단독 검증 테스트");

        // 0. 초기화
        scheduleTemplate.delete(failedKey);
        scheduleTemplate.delete(failDataKey);

        // 1. 임의의 실패 데이터 직접 주입 (3건)
        // Token 1: 1회 실패 상태 -> {token, retryCount:1, title, content}
        // Token 2: 2회 실패 상태 -> {token, retryCount:2, title, content}

        String[] testTokens = {
                TEST_TOKEN + "_solo1",
                TEST_TOKEN + "_solo2"
        };

        for (int i = 0; i < testTokens.length; i++) {
            String token = testTokens[i];
            int currentFailCount = i + 1; // 1, 2

            Map<String, Object> failInfo = new java.util.HashMap<>();
            failInfo.put("token", token);
            failInfo.put("retryCount", currentFailCount);
            failInfo.put("title", "단독 테스트 JSON 제목 " + currentFailCount);
            failInfo.put("content", "단독 테스트 JSON 내용 " + currentFailCount);

            try {
                String json = objectMapper.writeValueAsString(failInfo);
                scheduleTemplate.opsForSet().add(failedKey, json);
                log.info(">>> 데이터 주입(JSON) - Token: {}, Count: {}", token, currentFailCount);
            } catch (Exception e) {
                log.error(">>> JSON 직렬화 실패", e);
            }
        }

        // 2. Job 실행 (fcmRetryJob만 단독 실행)
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(fcmRetryJob, jobParameters);
        log.info("### Job 실행 완료 상태: {}", jobExecution.getStatus());

        // 3. 결과 확인
        // - 성공했으므로 FAIL_DATA에서 삭제되었는지 확인 (현재 validateOnly=true라 Firebase 성공 응답 시 삭제됨)
        // - 혹은 실패 코드를 시뮬레이션할 수 없으므로 성공으로 간주됨
        Set<Object> members = scheduleTemplate.opsForSet().members(failedKey);
        log.info("### 작업 후 Redis FAIL SET 상태: {}", members);
    }

    private void generateMockFailures(String scheduleKey, String failedKey, String token, int count) {
        String failDataKey = "SCHEDULE:FAIL_DATA:" + LocalDate.now();

        // 1. Hash 데이터 생성 (토큰을 키로 사용)
        CreateScheduleInputDto dto = new CreateScheduleInputDto();
        dto.setTitle("임의 실패 테스트 (" + token + ")");
        dto.setContent("데이터 생성 시간: " + System.currentTimeMillis());
        // FAIL_DATA에 직접 저장
        scheduleTemplate.opsForHash().put(failDataKey, token, dto);

        // 2. FAIL Set에 임의 데이터 주입 (예: TEST_TOKEN_1:1, TEST_TOKEN_2:2 등)
        for (int i = 1; i <= count; i++) {
            String id = token + "_" + i;
            // FAIL_DATA에도 같이 넣어줘야 Retry Writer에서 lookup 가능
            scheduleTemplate.opsForHash().put(failDataKey, id, dto);

            // 횟수는 1~2회로 섞어서 주입
            int retryCount = (i % 2) + 1;
            scheduleTemplate.opsForSet().add(failedKey, id + ":" + retryCount);
        }
    }

    @Test
    void FCM_고도화_재시도_테스트() throws Exception {
        LocalDate today = LocalDate.now();
        String scheduleKey = "SCHEDULE:" + today;
        String failedKey = "SCHEDULE:FAIL:" + today;

        log.info("### 1. 테스트용 임의 실패 데이터 10건 생성");
        generateMockFailures(scheduleKey, failedKey, TEST_TOKEN, 10);

        JobParameters parameter = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("targetStep", "fcmRetryStep")
                .toJobParameters();

        log.info("### 2. 고도화된 재시도 Job 실행 (단독 실행)");
        JobExecution execution = jobLauncher.run(fcmRetryJob, parameter);
        log.info("### Job 실행 상태: {}", execution.getStatus());

        // 3. 결과 확인
        Set<Object> members = scheduleTemplate.opsForSet().members(failedKey);
        log.info("### 3. 작업 후 Redis FAIL SET 상태 (횟수 증가 건들 확인): {}", members);
    }

    // @Test
    // void FCM_10만개_동기_전송_테스트() {
    // LocalDate localDate = LocalDate.now();
    // String scheduleKey = "SCHEDULE:" + localDate;
    //
    // log.info("hScan 기반 10만 건 동기(Sync) FCM 전송 테스트 시작 (Key: {})", scheduleKey);
    // long startTime = System.currentTimeMillis();
    // long totalProcessed = 0;
    //
    // // hScan을 통해 메모리 부하 없이 데이터 순회
    // try (var cursor = scheduleTemplate.opsForHash().scan(scheduleKey,
    // org.springframework.data.redis.core.ScanOptions.scanOptions().count(500).build()))
    // {
    //
    // while (cursor.hasNext()) {
    // Map.Entry<Object, Object> entry = cursor.next();
    // String id = entry.getKey().toString();
    // Object rawData = entry.getValue();
    // CreateScheduleInputDto dto = objectMapper.convertValue(rawData,
    // CreateScheduleInputDto.class);
    //
    // // 한 건씩 동기로 전송 (실패 시 FcmService 내부에서 Redis FAIL 키에 저장)
    // fcmService.sendNotification(id, rawData, TEST_TOKEN, dto.getTitle(),
    // dto.getContent(), true);
    //
    // totalProcessed++;
    // if (totalProcessed % 1000 == 0) {
    // log.info("현재까지 동기 전송 시도 완료: {} 건...", totalProcessed);
    // }
    // }
    // } catch (Exception e) {
    // log.error("동기 전송 루프 중 오류 발생", e);
    // }
    //
    // long endTime = System.currentTimeMillis();
    // double durationSeconds = (endTime - startTime) / 1000.0;
    // log.info("================================================");
    // log.info("🏁 동기 전송 테스트 종료");
    // log.info("📊 총 시도 건수: {} 건", totalProcessed);
    // log.info("⏱️ 총 소요 시간: {} 초", durationSeconds);
    // log.info("⏱️ 건당 평균 속도: {} ms", totalProcessed > 0 ? (endTime - startTime) /
    // (double) totalProcessed : 0);
    // log.info("================================================");
    // }
}
