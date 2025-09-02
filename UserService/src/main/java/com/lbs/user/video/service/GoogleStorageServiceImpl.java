package com.lbs.user.video.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 작성자  : lbs
 * 날짜    : 2025-08-30
 * 구글 클라우드 스토리지에 업로드 및 db 에 데이터 저장
 **/


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleStorageServiceImpl implements StorageService {
    private final Storage storage;

    @Override
    public String uploadVideo(MultipartFile file) {
       String bucketName = "evil55";
       String objectName = "videos/" + file.getOriginalFilename();

       try{
           BlobId blobId = BlobId.of(bucketName, objectName);
           BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                   .setContentType(file.getContentType())
                   .build();

           //  MultipartFile의 InputStream을 사용해 바로 업로드
           Blob blob  = storage.createFrom(blobInfo, file.getInputStream());
           log.info("구글 스토리지 {}에  파일 업로드 완료 {}", bucketName , objectName );

           // 객체(파일)의 공개 URL 생성
           // GCS Blob 객체의 서명되지 않은 공개 URL(Unsigned Public URL) 생성
           String publicUrl =  blob.getMediaLink();
           return publicUrl;

       } catch (IOException e) {
           log.error("구글 스토리지 {}에  파일 업로드 실패 {}", bucketName , objectName );
           throw new RuntimeException("파일 업로드 실패", e);
       }
    }

}
