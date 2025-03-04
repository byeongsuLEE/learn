package com.lbs.gatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-04
 * 설명 : gateway에 등록되어 있는 모든 서비스에 적용하는 공통필터 클래스
 **/
@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter(){
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global filter baseMessage : -> {}" , config.baseMessage);

            if(config.preLogger){
                log.info("Global filter start : request id -> {}" , request.getId());
            }


            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.postLogger){
                    log.info("Global filter end: response code -> {}", response.getStatusCode());
                    }
            }));

        };
    }


    @Data
    public static class Config{
        // yml에 있는 데이터가져올꺼임
        private String  baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }

}
