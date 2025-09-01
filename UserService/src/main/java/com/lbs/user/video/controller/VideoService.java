package com.lbs.user.video.controller;

import com.lbs.user.video.dto.request.VideoUploadDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    String uploadVideo(MultipartFile file);
    void createVideo(VideoUploadDto videoUploadDto, String fileUrl);
}
