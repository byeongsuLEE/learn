package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.video.domain.Video;
import com.lbs.user.video.infrastructure.entity.VideoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-02
 * 풀이방법
 **/


@Transactional
@Repository
@RequiredArgsConstructor
public class JPAVideoRepositoryImpl implements VideoRepository {

    private final VideoJPARepository videoJPARepository;

    @Override
    public Video saveVideo(Video video
    ) {
        //domain 객체 만들고  ENTITY로 설정
        VideoEntity videoEntity = VideoEntity.builder()
                .title(video.getTitle())
                .description(video.getDescription())
                .tag(video.getTag())
                .userId(video.getUserId())
                .url(video.getUrl())
                .build();

        VideoEntity savedEntity = videoJPARepository.save(videoEntity);

        return getVideo(savedEntity);

    }

    @Override
    public void deleteVideo(Long videoId) {

    }

    @Override
    public Video getVideo(Long videoId) {
        VideoEntity videoEntity = videoJPARepository.findById(videoId).orElseGet(()-> null);
        return getVideo(videoEntity);

    }

    private static Video getVideo(VideoEntity videoEntity) {
        AuditInfo auditInfo = getAuditInfo(videoEntity);
        return Video.createVideo(videoEntity.getId(), videoEntity.getTitle(),
                videoEntity.getDescription(), videoEntity.getTag(),
                videoEntity.getUrl(), videoEntity.getUserId(), auditInfo);
    }

    @Override
    public List<Video> getAllVideos() {
        return List.of();
    }

    private static AuditInfo getAuditInfo(VideoEntity savedEntity) {
        return AuditInfo.builder()
                .createdBy(savedEntity.getLastModifiedBy())
                .createdDate(savedEntity.getCreatedDate())
                .lastModifiedDate(savedEntity.getLastModifiedDate())
                .lastModifiedDate(savedEntity.getLastModifiedDate())
                .build();
    }
}
