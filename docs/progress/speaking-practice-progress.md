# 말하기 연습 기능 진행 기록

## 2026-05-12

## 구현 완료 범위

- 백엔드 `SpeakingService` MVP를 구현했다.
- 포함된 기능:
  - 오늘 질문 조회 및 fallback 생성;
  - MinIO presigned upload URL 발급;
  - Redis 업로드 세션 저장;
  - DB에는 presigned URL이 아니라 `objectKey`만 저장;
  - RabbitMQ 비동기 분석 메시지 발행;
  - RabbitMQ worker 기반 Gemini 분석;
  - Redis 진행률 조회;
  - 분석 결과 MySQL 저장;
  - 재분석;
  - soft delete;
  - temp object cleanup job;
  - stale analysis recovery job.
- 프론트엔드 작업도 병렬로 진행했다.
  - `/speaking` MVP 화면 추가;
  - `services/speaking.ts` API 연결 추가;
  - Playwright mock E2E 테스트 추가.
- Config Server repository `master`에 설정 파일을 추가했다.
  - `learn-speaking-local.yml`;
  - `learn-speaking-prod.yml`.
- Gemini API key는 평문으로 저장하지 않고 Config Server `{cipher}` 값으로만 저장했다.

## 로컬 기동 중 발견하고 수정한 문제

- 기존 `learn` schema가 비어 있지 않아 Flyway가 시작되지 않았다.
  - `spring.flyway.baseline-on-migrate: true` 추가;
  - `spring.flyway.baseline-version: 0` 추가.
- config prefix가 코드와 맞지 않았다.
  - `speaking.rabbit`을 `speaking.rabbitmq`로 수정.
- 로컬 실행 시 Eureka `127.0.0.1:8761` 등록 경고가 반복됐다.
  - `learn-speaking-local.yml`에서 Eureka client를 비활성화했다.
- Gemini endpoint 설정이 코드 호출 방식과 맞지 않았다.
  - endpoint를 전체 path가 아니라 base URL인 `https://generativelanguage.googleapis.com`로 수정했다.
- PowerShell `Invoke-WebRequest`가 MinIO presigned `PUT`에서 `NullReferenceException`을 냈다.
  - smoke 검증에서는 .NET `HttpClient` 방식으로 MinIO PUT을 수행했다.

## 테스트 검증

- `SpeakingService` 단위/슬라이스 테스트:
  - `./gradlew.bat test` 통과.
- `SpeakingService` 통합 테스트:
  - `./gradlew.bat integrationTest` 통과.
- 프론트엔드 검증:
  - `pnpm type-check` 통과;
  - `pnpm build` 통과;
  - `pnpm test:e2e tests/e2e/speaking.spec.ts` 통과.

## 인증 포함 Full Smoke

- `SpeakingService`를 `local` profile로 로컬 실행했다.
- 서버 인프라를 사용했다.
  - MySQL;
  - Redis;
  - RabbitMQ;
  - MinIO;
  - Config Server;
  - Gemini API.
- 채팅에서 제공받은 실제 사용자 JWT로 검증했다.
- JWT는 파일에 저장하지 않았고 커밋하지 않았다.
- 검증 결과:
  - `/actuator/health`가 `UP`을 반환했다;
  - `GET /speaking/today`가 오늘 질문을 반환했다;
  - `POST /speaking/uploads/presigned-url`이 업로드 정보를 반환했다;
  - MinIO presigned `PUT`이 `200`을 반환했다;
  - `POST /speaking/records`가 `201`을 반환했다;
  - RabbitMQ worker가 분석 메시지를 소비했다;
  - Gemini 분석이 완료됐다;
  - progress가 `100`에 도달했다;
  - record detail에 analysis 데이터가 포함됐다;
  - audio presigned URL이 반환됐다.
- 최종 smoke 결과:
  - `COMPLETED`.

## 다음 체크포인트

- 배포 전에는 아래를 추가 확인한다.
  - 서버 `docker-compose.yml`에 `speaking` 서비스 추가;
  - Nginx에서 `/speaking-audio/**`를 MinIO로 프록시;
  - Gateway에서 `/api/speaking-service/**` 라우팅 확인;
  - Eureka에 `SPEAKING-SERVICE` 등록 확인;
  - 운영 도메인 기준 smoke test.

## 서버 배포 준비

- `lbs-server`의 `/home/lbs/docker-compose.yml`을 백업했다.
- `lbs-server`의 `/home/lbs/nginx/conf.d/default.conf`를 백업했다.
- 서버 `docker-compose.yml`에 `speaking` 서비스를 추가했다.
  - image: `evil55/speaking`;
  - container name: `speaking`;
  - container port: `8083`;
  - host port: `8084`;
  - profile: `prod`;
  - config server: `http://config-server:8888`;
  - Eureka: `http://discovery:8761/eureka`.
- host `8083`은 `kafka-connect`가 이미 사용 중이라 `speaking`은 host `8084:8083`으로 열었다.
- Nginx에 `/speaking-audio/` 프록시를 추가했다.
  - public URL: `https://evil55.cloud/speaking-audio/...`;
  - upstream: `http://minio:9000/speaking-audio/...`.
- 검증:
  - `docker compose -f /home/lbs/docker-compose.yml config --quiet` 통과;
  - `docker exec nginx nginx -t` 통과;
  - `docker exec nginx nginx -s reload` 완료;
  - `docker compose config --services`에서 `speaking` 인식 확인.
- 아직 `speaking` 컨테이너는 올리지 않았다.
  - 이유: 현재 로컬 repo의 `SpeakingService/`가 untracked 상태라 Jenkins에서 배포하려면 먼저 GitHub `learn` repository에 코드가 들어가야 한다.
