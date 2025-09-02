package com.lbs.user.video.controller;

import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.video.domain.Video;
import com.lbs.user.video.dto.request.VideoUploadDto;
import com.lbs.user.video.dto.response.VideoResponseDto;
import com.lbs.user.video.service.StorageService;
import com.lbs.user.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-08-27
 * 풀이방법
 **/


@Slf4j
@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final StorageService storageService;
    private final VideoService videoService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<VideoResponseDto>> uploadVideo (@ModelAttribute VideoUploadDto videoUploadDto) {
        log.info(videoUploadDto.toString());

        String url = storageService.uploadVideo(videoUploadDto.videoFile());
        log.info(url.toString());

        Video video = videoService.saveVideo(videoUploadDto, url);
        VideoResponseDto responseDto = new VideoResponseDto(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getTag(),
                video.getUrl(),
                video.getUserId(),
                video.getAuditInfo()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED,responseDto));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<VideoResponseDto>> getVideo (Long id) {



        Video video = videoService.getVideo(id);
        VideoResponseDto responseDto = new VideoResponseDto(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getTag(),
                video.getUrl(),
                video.getUserId(),
                video.getAuditInfo()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED,responseDto));
    }

}
