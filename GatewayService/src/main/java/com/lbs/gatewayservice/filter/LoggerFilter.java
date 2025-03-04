package com.lbs.gatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-04
 * 설명    : 하나의 서비스에 logging filter 등록
 **/
@Component
@Slf4j
public class LoggerFilter extends AbstractGatewayFilterFactory<LoggerFilter.Config> {
    public LoggerFilter(){
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        //exchange : 서버 request,response를 가져오도록 도와주는 것
        GatewayFilter gatewayFilter  = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("logging filter baseMessage : -> {}" , config.baseMessage);

            if(config.preLogger){
                log.info("logging filter start : request id -> {}" , request.getId());
            }


            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.postLogger){
                    log.info("logging filter end: response code -> {}", response.getStatusCode());
                }
            }));
        }, OrderedGatewayFilter.LOWEST_PRECEDENCE); // 우선순위를 가장 높거나 낮게할수있음

        return gatewayFilter;
    }


    @Data
    public static class Config{
        // yml에 있는 데이터가져올꺼임
        private String  baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }

}
