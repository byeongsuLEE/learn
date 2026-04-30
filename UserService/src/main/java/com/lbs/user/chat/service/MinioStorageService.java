package com.lbs.user.chat.service;

import com.lbs.user.chat.dto.response.PresignedUrlResponseDto;

public interface MinioStorageService {
    PresignedUrlResponseDto generateUploadUrl(String fileType, Long roomId, Long userId);
}
