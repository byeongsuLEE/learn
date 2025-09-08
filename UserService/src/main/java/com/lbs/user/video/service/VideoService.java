package com.lbs.user.video.service;

import com.lbs.user.video.domain.Video;
import com.lbs.user.video.dto.request.VideoSearchDto;
import com.lbs.user.video.dto.request.VideoUploadDto;
import com.lbs.user.video.dto.response.VideoResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface VideoService {
    Video saveVideo(VideoUploadDto videoUploadDto, String storageURL, String thumbnailImageURL);
    Video getVideo(Long id);

    Slice<VideoResponseDto> getVideoList(VideoSearchDto videoSearchDto, Pageable pageable);
}
