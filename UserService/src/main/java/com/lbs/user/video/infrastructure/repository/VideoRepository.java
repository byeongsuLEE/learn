package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.video.domain.Video;
import com.lbs.user.video.dto.request.VideoSearchDto;
import com.lbs.user.video.dto.response.VideoResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface VideoRepository {

    Video saveVideo(Video video);
    void deleteVideo(Long videoId);
    Video getVideo(Long videoId);
    Slice<VideoResponseDto> getAllVideos(VideoSearchDto videoSearchDto, Pageable pageable);
}
