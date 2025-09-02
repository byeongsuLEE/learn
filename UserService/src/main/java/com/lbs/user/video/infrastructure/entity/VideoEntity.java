package com.lbs.user.video.infrastructure.entity;

import com.lbs.user.user.infrastructure.entity.BaseEntity;
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

}
