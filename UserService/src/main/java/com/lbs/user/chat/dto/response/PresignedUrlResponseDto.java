package com.lbs.user.chat.dto.response;

import lombok.*;

@Data @Builder
public class PresignedUrlResponseDto {
    private String uploadUrl;
    private String fileUrl;
    private int expiresIn;
}
