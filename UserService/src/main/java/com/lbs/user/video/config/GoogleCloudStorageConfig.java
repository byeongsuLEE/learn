package com.lbs.user.video.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class GoogleCloudStorageConfig  {
    @Value("${google.cloud.storage.credentials.location}")
    private String credentialsLocation;

    @Bean
    public Storage storage() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(getClass().getResourceAsStream(credentialsLocation));

        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}