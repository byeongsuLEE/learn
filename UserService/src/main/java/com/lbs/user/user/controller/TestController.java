package com.lbs.user.user.controller;

import com.lbs.user.user.common.mapper.UserMapper;
import com.lbs.user.user.common.response.ApiResponse;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.dto.request.UserJoinRequestDto;
import com.lbs.user.user.dto.response.UserJoinResponseDto;
import com.lbs.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lbs.user.user.common.response.ApiResponse.success;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-02
 * 풀이방법
 **/

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class TestController {

    @PostMapping("/joins")
    public ResponseEntity<ApiResponse<UserJoinResponseDto>> joinUser (@RequestBody UserJoinRequestDto userJoinRequestDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,null));

    }

}
