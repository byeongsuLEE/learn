package com.lbs.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-03
 * gateway 환경 구성 세팅하기 (yml 대신 하는 방법)
 **/


//@Configuration
public class FilterConfig {
//    @Bean
    public RouteLocator gatewayRouters(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/api/user-service/**")
                        .filters(f->f.addRequestHeader("user-request","user-request-header")
                                .addResponseHeader("user-request","user-request-header"))
                        .uri("http://localhost:8081"))
                .build();
    }
}
