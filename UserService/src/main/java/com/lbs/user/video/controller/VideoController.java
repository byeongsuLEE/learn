package com.lbs.user.video.controller;

import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.video.dto.request.VideoUploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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
    private final VideoService videoService ;

    @PostMapping("")
    public ResponseEntity<ApiResponse<String>> uploadVideo (@ModelAttribute VideoUploadDto videoUploadDto) {
        log.info(videoUploadDto.toString());

        String url = videoService.uploadVideo(videoUploadDto.videoFile());
        log.info(url.toString());



        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED,"동영상 업로드 완료"));
    }

}
