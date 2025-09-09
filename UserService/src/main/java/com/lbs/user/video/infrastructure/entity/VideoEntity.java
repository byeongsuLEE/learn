package com.lbs.user.video.infrastructure.entity;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.user.infrastructure.entity.BaseEntity;
import com.lbs.user.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-02
 * video 엔티티
 **/


@Entity
@Table(name = "videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VideoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private String tag;
    private String videoURL;
    private String thumbnailURL;


    @Builder
    public VideoEntity(Long id, String title, String description, Long userId, String tag, String videoURL, String thumbnailURL) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.tag = tag;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;

    }

    public Video toDomain() {
        AuditInfo auditInfo = new AuditInfo(getCreatedBy(),getCreatedDate(),getLastModifiedBy(),getLastModifiedDate());
        return Video.createVideo(
                this.id,
                this.title,
                this.description,
                this.tag,
                this.videoURL,
                this.thumbnailURL,
                this.userId,
                auditInfo
        );
    }

    @Override
    public String toString() {
        return "VideoEntity{" + "id=" + id + ", title=" + title + ", description=" + description + ", userId=" + userId + ", tag=" + tag + ", videoURL=" + videoURL + "}";
    }
}
