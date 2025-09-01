package com.lbs.user.video.service;

import com.lbs.user.video.dto.request.VideoUploadDto;
import com.lbs.user.video.repository.VideoRepository;
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
    public void saveVideo(VideoUploadDto videoUploadDto, String storageURL) {

    }
}
