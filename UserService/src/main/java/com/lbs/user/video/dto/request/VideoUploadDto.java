package com.lbs.user.video.dto.request;

import org.springframework.web.multipart.MultipartFile;

// 코드 한 줄로 DTO 완성
public record VideoUploadDto(Long userId, String title, String description, MultipartFile videoFile) {}
