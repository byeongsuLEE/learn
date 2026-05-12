package com.lbs.speaking.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinioConfig {

    @Bean
    @Primary
    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.endpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .build();
    }

    @Bean
    @Qualifier("presignedMinioClient")
    public MinioClient presignedMinioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.presignedEndpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .build();
    }
}
