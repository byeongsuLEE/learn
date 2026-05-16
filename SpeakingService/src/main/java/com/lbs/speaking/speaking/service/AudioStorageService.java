package com.lbs.speaking.speaking.service;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.MinioProperties;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import io.minio.messages.Item;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AudioStorageService {

    private static final String DEFAULT_REGION = "us-east-1";

    private final MinioClient minioClient;
    private final MinioClient presignedMinioClient;
    private final MinioProperties properties;

    public AudioStorageService(
            MinioClient minioClient,
            @Qualifier("presignedMinioClient") MinioClient presignedMinioClient,
            MinioProperties properties
    ) {
        this.minioClient = minioClient;
        this.presignedMinioClient = presignedMinioClient;
        this.properties = properties;
    }

    public String createUploadUrl(String objectKey) {
        try {
            return presignedMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(properties.bucket())
                    .region(DEFAULT_REGION)
                    .object(objectKey)
                    .expiry(properties.presignedPutExpiryMinutes(), TimeUnit.MINUTES)
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.STORAGE_OPERATION_FAILED, exception);
        }
    }

    public String createDownloadUrl(String objectKey) {
        try {
            return presignedMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(properties.bucket())
                    .region(DEFAULT_REGION)
                    .object(objectKey)
                    .expiry(properties.presignedGetExpiryMinutes(), TimeUnit.MINUTES)
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.STORAGE_OPERATION_FAILED, exception);
        }
    }

    public void assertExists(String objectKey) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.AUDIO_OBJECT_NOT_FOUND, exception);
        }
    }

    public void copy(String sourceKey, String destinationKey) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(destinationKey)
                    .source(CopySource.builder()
                            .bucket(properties.bucket())
                            .object(sourceKey)
                            .build())
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.STORAGE_OPERATION_FAILED, exception);
        }
    }

    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.STORAGE_OPERATION_FAILED, exception);
        }
    }

    public List<String> findTempObjectsOlderThan(Instant threshold) {
        List<String> objectKeys = new ArrayList<>();
        try {
            Iterable<io.minio.Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(properties.bucket())
                    .prefix("speaking/temp/")
                    .recursive(true)
                    .build());
            for (io.minio.Result<Item> result : results) {
                Item item = result.get();
                if (item.lastModified() != null && item.lastModified().toInstant().isBefore(threshold)) {
                    objectKeys.add(item.objectName());
                }
            }
            return objectKeys;
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.STORAGE_OPERATION_FAILED, exception);
        }
    }
}
