package com.lbs.order.order.controller;

import com.lbs.order.order.feign.response.UserResponse;
import com.lbs.order.order.response.ApiResponse;
import com.lbs.order.order.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<UserResponse>> getUser() {
        UserResponse user = orderService.getUser(1L);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,user));
    }

}
