package com.lbs.user.video.dto.response;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.video.domain.Video;

public record VideoResponseDto(
        Long id,
        String title,
        String description,
        String tag,
        String url,
        Long userId,
        AuditInfo auditInfo
) {
    public static VideoResponseDto fromDomain(Video video) {
        return new VideoResponseDto(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getTag(),
                video.getUrl(),
                video.getUserId(),
                video.getAuditInfo());
    };
}
