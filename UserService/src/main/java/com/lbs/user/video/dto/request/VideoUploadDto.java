package com.lbs.user.video.dto.request;

import org.springframework.web.multipart.MultipartFile;

// 코드 한 줄로 DTO 완성
public record VideoUploadDto(String title, String description,String tag, String url, MultipartFile videoFile, Long userId) {}
