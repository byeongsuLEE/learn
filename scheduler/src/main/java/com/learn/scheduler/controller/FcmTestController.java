package com.learn.scheduler.controller;

import com.learn.scheduler.service.FcmService;
import com.learn.scheduler.service.RedisDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class FcmTestController {

    private final FcmService fcmService;
    private final RedisDataGenerator redisDataGenerator;

    @GetMapping("/fcm")
    public String testFcm(
            @RequestParam String token,
            @RequestParam(defaultValue = "Test Title") String title,
            @RequestParam(defaultValue = "Test Body") String body,
            @RequestParam(defaultValue = "true") boolean validateOnly) {

        fcmService.sendNotificationAsync(token, title, body, validateOnly);
        return "Async FCM activation request sent. Check logs for results.";
    }

    @GetMapping("/redis-populate")
    public String populateRedis(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam String token) {

        redisDataGenerator.saveTestUser(userId, token);
        redisDataGenerator.generate1000SchedulesForToday();

        return "Redis population (User + 1000 fields for today) started/completed. Check logs.";
    }
}
