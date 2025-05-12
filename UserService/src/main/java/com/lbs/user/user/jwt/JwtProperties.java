package com.lbs.user.user.jwt;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-08
 * 풀이방법
 **/


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long accessExpirationTime;
    private long refreshExpirationTime;
    private String tokenPrefix = "Bearer";
    private String headerName = "Authorization";
}
