# 기본 OpenJDK 17 이미지를 사용
FROM openjdk:17

# /app 디렉토리로 작업 디렉토리 설정
WORKDIR /app

Run ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
echo "Asia/Seoul" > /etc/timezone

# JAR 파일을 app.jar로 복사
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 컨테이너 외부 통신을 위한 포트 설정
EXPOSE 8080

# 컨테이너 시작 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
# user 테스트용712
