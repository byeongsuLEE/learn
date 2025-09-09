package com.lbs.user.video.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Profile("!local") // local 프로파일이 아닐 때 이 빈을 사용
public class GoogleCloudStorageProdConfig {

    @Value("${google.cloud.storage.credentials.location}")
    private String credentialsLocation;

    @Bean
    public Storage storage() throws IOException {
        // Jenkins 환경: 시스템 프로퍼티로 받은 경로의 파일을 직접 읽기
        FileInputStream serviceAccountStream = new FileInputStream(credentialsLocation);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);

        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}