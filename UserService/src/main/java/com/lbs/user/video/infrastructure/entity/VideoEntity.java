package com.lbs.user.video.infrastructure.entity;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.user.infrastructure.entity.BaseEntity;
import com.lbs.user.video.domain.Video;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-02
 * 풀이방법
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
    private String url;


    @Builder
    public VideoEntity(Long id, String title, String description, Long userId, String tag, String url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.tag = tag;
        this.url = url;

    }

    @Override
    public String toString() {
        return "VideoEntity{" + "id=" + id + ", title=" + title + ", description=" + description + ", userId=" + userId + ", tag=" + tag + ", url=" + url + "}";
    }

    public Video toDomain() {
        AuditInfo auditInfo = new AuditInfo(getCreatedBy(),getCreatedDate(),getLastModifiedBy(),getLastModifiedDate());

        return Video.createVideo(
                this.id,
                this.title,
                this.description,
                this.tag,
                this.url,
                this.userId,
                auditInfo
        );
    }
}
