package com.lbs.user.video.dto.response;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.video.domain.Video;
import com.lbs.user.video.infrastructure.entity.VideoEntity;

public record VideoResponseDto(
        Long id,
        String title,
        String description,
        String tag,
        String videoURL,
        String thumbURL,
        Long userId,
        AuditInfo auditInfo
) {
    public static VideoResponseDto fromDomain(Video video) {
        return new VideoResponseDto(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getTag(),
                video.getVideoURL(),
                video.getThumbnailURL(),
                video.getUserId(),
                video.getAuditInfo());
    };

    public static VideoResponseDto fromEntity(VideoEntity videoEntity) {

        AuditInfo auditInfo = new AuditInfo(
                videoEntity.getCreatedBy(),
                videoEntity.getCreatedDate(),
                videoEntity.getLastModifiedBy(),
                videoEntity.getLastModifiedDate()
        );

        return new VideoResponseDto(
                videoEntity.getId(),
                videoEntity.getTitle(),
                videoEntity.getDescription(),
                videoEntity.getTag(),
                videoEntity.getVideoURL(),
                videoEntity.getThumbnailURL(),
                videoEntity.getUserId(),
                auditInfo);

    };
}
