package com.lbs.user.video.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface VideoService {
    String uploadVideo(MultipartFile file);
    void createVideo(VideoUploadDto videoUploadDto, String fileUrl);
}
