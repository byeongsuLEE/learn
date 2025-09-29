package com.lbs.user.video.dto.response;

import java.time.LocalDateTime;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-18
 * 풀이방법
 **/


public record VideoQueryDto (
        Long id,
        String title,
        String description,
        String tag,
        String videoURL,
        String thumbnailURL,
        Long userId,
        String createdBy,
        LocalDateTime createdDate,
        String lastModifiedBy,
        LocalDateTime lastModifiedDate

){
}
