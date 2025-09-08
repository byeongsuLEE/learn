package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.video.domain.Video;
import com.lbs.user.video.dto.request.VideoSearchDto;
import com.lbs.user.video.dto.response.VideoResponseDto;
import com.lbs.user.video.infrastructure.entity.VideoEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.lbs.user.video.infrastructure.entity.QVideoEntity.videoEntity;

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
    private final JPAQueryFactory queryFactory;


    @Override
    public Video saveVideo(Video video
    ) {
        //domain 객체 만들고  ENTITY로 설정
        VideoEntity videoEntity = VideoEntity.builder()
                .title(video.getTitle())
                .description(video.getDescription())
                .tag(video.getTag())
                .userId(video.getUserId())
                .videoURL(video.getVideoURL())
                .thumbnailURL(video.getThumbnailURL())
                .build();

        VideoEntity savedEntity = videoJPARepository.save(videoEntity);

        return getVideo(savedEntity);

    }

    @Override
    public void deleteVideo(Long videoId) {

    }

    @Override
    public Video getVideo(Long videoId) {
        VideoEntity videoEntity = videoJPARepository.findById(videoId).orElseGet(() -> null);
        return getVideo(videoEntity);

    }

    private static Video getVideo(VideoEntity videoEntity) {
        AuditInfo auditInfo = getAuditInfo(videoEntity);
        return Video.createVideo(videoEntity.getId(), videoEntity.getTitle(),
                videoEntity.getDescription(), videoEntity.getTag(),
                videoEntity.getVideoURL(), videoEntity.getThumbnailURL(), videoEntity.getUserId(), auditInfo);
    }

    @Override
    public Slice<VideoResponseDto> getAllVideos(VideoSearchDto videoSearchDto, Pageable pageable) {


        BooleanExpression searchCondition = createSearchCondition(videoSearchDto);

        List<VideoEntity> entityList = queryFactory.selectFrom(videoEntity)
                .where(searchCondition)
                .orderBy(videoEntity.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // 1. hasNext 계산
        boolean hasNext = entityList.size() > pageable.getPageSize();

        // 2. hasNext가 true이면 마지막 항목 제거
        if (hasNext) {
            entityList.remove(pageable.getPageSize());
        }

        List<Video> videoList = entityList.stream()
                .map(VideoEntity::toDomain) // toDomain 메서드 추가
                .collect(Collectors.toList());


        List<VideoResponseDto> dtoList = videoList.stream()
                .map(VideoResponseDto::fromDomain)
                .collect(Collectors.toList());


        return new SliceImpl<>(dtoList, pageable, hasNext);
    }

    private BooleanExpression createSearchCondition(VideoSearchDto videoSearchDto) {
        String keyword = videoSearchDto.keyword();

        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        // OR 조건을 직접 연결하여 하나의 BooleanExpression으로 반환
        return videoEntity.title.containsIgnoreCase(keyword)
                .or(videoEntity.description.containsIgnoreCase(keyword))
                .or(videoEntity.tag.containsIgnoreCase(keyword));
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
