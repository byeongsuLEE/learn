package com.lbs.user.video.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface VideoService {
    void uploadVideo(MultipartFile file);
}
