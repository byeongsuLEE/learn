package com.lbs.user.video.service;

import com.lbs.user.video.domain.Video;
import com.lbs.user.video.dto.request.VideoUploadDto;

public interface VideoService {
    Video saveVideo(VideoUploadDto videoUploadDto, String storageURL);
    Video getVideo(Long id);


}
