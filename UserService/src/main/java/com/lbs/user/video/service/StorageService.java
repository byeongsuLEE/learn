package com.lbs.user.video.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadVideo(MultipartFile file);
}
