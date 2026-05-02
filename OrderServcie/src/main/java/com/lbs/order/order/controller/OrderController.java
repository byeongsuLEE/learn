package com.lbs.order.order.controller;

import com.lbs.order.order.feign.response.UserResponse;
import com.lbs.order.order.response.ApiResponse;
import com.lbs.order.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-24
 * 풀이방법
 **/


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final ServletWebServerApplicationContext servletWebServerApplicationContext;

    @Value("${server.port}")
    String serverPort;


    @GetMapping("/heath-check")
    public String healthCheck() {
        StringBuilder sb = new StringBuilder();

        sb.append("이 서버의 포트는 ").append(servletWebServerApplicationContext.getWebServer().getPort())
                .append("서버의 이름은 ").append(servletWebServerApplicationContext.getApplicationName());
        return sb.toString();
    }
    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<UserResponse>> getUser() {
        UserResponse user = orderService.getUser(1L);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,user));
    }

}
