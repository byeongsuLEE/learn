package com.lbs.user.common.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 작성자  : lbs
 * 날짜    : 2025-06-03
 * 풀이방법
 **/


@Component
public class ConfigChecker {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void logConfig() {
        System.out.println("=== CONFIG VALUES ===");
        System.out.println("Username: [" + username + "]");
        System.out.println("Password: [" + password + "]");
        System.out.println("Username starts with {cipher}: " + username.startsWith("{cipher}"));
        System.out.println("====================");
    }
}