package com.lbs.user.video.repository;

import com.lbs.user.video.dto.request.VideoUploadDto;
import com.lbs.user.video.dto.response.VideoDto;

import java.util.List;

public interface VideoRepository {

    String saveVideo(VideoUploadDto videoUploadDto);
    void deleteVideo(String videoId);
    VideoDto getVideo(String videoId);
    List<VideoDto> getAllVideos();
}
