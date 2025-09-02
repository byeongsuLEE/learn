package com.lbs.user.video.domain;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.video.dto.request.VideoUploadDto;
import lombok.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-02
 * 풀이방법
 **/



@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Video {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private String url;
    private Long userId;
    private AuditInfo auditInfo;

    static public Video createVideo(VideoUploadDto videoUploadDto,String url) {
        return Video.builder()
                .title(videoUploadDto.title())
                .description(videoUploadDto.description())
                .tag(videoUploadDto.tag())
                .url(url)
                .userId(videoUploadDto.userId())
                .build();
    }
    static public Video createVideo(Long id, String title, String description, String tag, String url, Long userId, AuditInfo auditInfo) {
        return Video.builder()
                .id(id)
                .title(title)
                .description(description)
                .tag(tag)
                .url(url)
                .userId(userId)
                .auditInfo(auditInfo)
                .build();
    }

}
