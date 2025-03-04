package com.lbs.user.presentation.controller;

import com.lbs.user.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
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
@RequiredArgsConstructor
public class UserController {

    private final Environment env;

    @GetMapping("/welcome")
    public ResponseEntity<ApiResponse<String>> gatewayConnectTest (){

        log.info("들어왔나?");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"테스트 성공"));

    }
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> gatewayCustomFilterTest (){

        log.info("customfilter check ");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"customfilter success "));

    }
    @GetMapping("/port-check")
    public ResponseEntity<ApiResponse<String>> gatewayPortCheck (){

        log.info("customfilter check ");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,
                        String.format("서버 포트는 : %s", env.getProperty("local.server.port"))));

    }

}
