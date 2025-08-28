package com.lbs.user.video.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface VideoService {
    void uploadVideo(MultipartFile file);
}
