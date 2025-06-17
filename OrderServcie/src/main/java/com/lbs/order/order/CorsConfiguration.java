package com.lbs.order.order;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-24
 * 풀이방법
 **/


@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 적용
                .allowedOrigins( // 허용할 도메인들
                        "http://localhost:3000",    // 로컬 React 개발서버
                        "http://localhost:3001",    // 다른 포트
                        "https://evil55.shop"      // 운영 도메인
                              // 다른 운영 도메인
                )
                .allowedMethods( // 허용할 HTTP 메서드
                        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
                )
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키/인증정보 허용
                .maxAge(3600); // preflight 캐시 시간 (1시간)
    }
}
