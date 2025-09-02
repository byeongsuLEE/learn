package com.lbs.user.video.infrastructure.repository;

import com.lbs.user.video.domain.Video;

import java.util.List;

public interface VideoRepository {

    Video saveVideo(Video video);
    void deleteVideo(Long videoId);
    Video getVideo(Long videoId);
    List<Video> getAllVideos();
}
