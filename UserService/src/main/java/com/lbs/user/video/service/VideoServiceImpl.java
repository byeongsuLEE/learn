package com.lbs.user.video.service;

import com.lbs.user.video.domain.Video;
import com.lbs.user.video.dto.request.VideoUploadDto;
import com.lbs.user.video.infrastructure.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-09-01
 * 풀이방법
 **/


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    @Override
    public Video saveVideo(VideoUploadDto videoUploadDto, String storageURL) {
        // 도메인 객체 만들어서 넘겨주기
        Video video = Video.createVideo(videoUploadDto, storageURL);
        return videoRepository.saveVideo(video);
    }

    @Override
    public Video getVideo(Long id) {
        return videoRepository.getVideo(id);
    }
}
