package com.lbs.user.video.service;

import com.lbs.user.video.dto.request.VideoUploadDto;

public interface VideoService {
    void saveVideo(VideoUploadDto videoUploadDto, String storageURL);
}
