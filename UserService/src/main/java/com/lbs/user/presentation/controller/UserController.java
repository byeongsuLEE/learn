package com.lbs.user.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

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
    public String gatewayConnectTest (){

        log.info("들어왔나?");
        return "success";
    }
}
