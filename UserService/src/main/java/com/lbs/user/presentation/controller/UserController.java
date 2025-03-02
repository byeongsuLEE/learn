package com.lbs.user.presentation.controller;

import com.lbs.user.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lbs.user.common.response.ApiResponse.success;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-02
 * 풀이방법
 **/


@RestController
@RequestMapping
@Slf4j
public class UserController {

    @GetMapping("/welcome")
    public ResponseEntity<ApiResponse<String>> gatewayConnectTest (){

        log.info("들어왔나?");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"테스트 성공"));

    }
}
