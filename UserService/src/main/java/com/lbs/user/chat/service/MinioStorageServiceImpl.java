package com.lbs.user.chat.service;

import com.lbs.user.chat.dto.response.PresignedUrlResponseDto;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageServiceImpl implements MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket:chat-images}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public PresignedUrlResponseDto generateUploadUrl(String fileType, Long roomId, Long userId) {
        String ext = resolveExtension(fileType);
        String objectKey = "chat/" + roomId + "/" + userId + "/" + UUID.randomUUID() + ext;

        try {
            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(5, TimeUnit.MINUTES)
                            .build()
            );
            String fileUrl = endpoint + "/" + bucket + "/" + objectKey;
            return PresignedUrlResponseDto.builder()
                    .uploadUrl(uploadUrl)
                    .fileUrl(fileUrl)
                    .expiresIn(300)
                    .build();
        } catch (Exception e) {
            log.error("MinIO presigned URL 생성 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드 URL 생성에 실패했습니다", e);
        }
    }

    private String resolveExtension(String fileType) {
        return switch (fileType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
