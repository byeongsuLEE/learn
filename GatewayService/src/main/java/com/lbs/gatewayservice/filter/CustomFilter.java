package com.lbs.gatewayservice.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-03
 * 풀이방법
 **/


@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom     filter : request id ->" +
                    "{}" , request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom filter : response code -> {}", response.getStatusCode());
            }));

        };
    }

    public static class Config{

    }

}
