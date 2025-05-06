package com.lbs.user.user.config;

import com.lbs.user.user.security.CustomOAuth2AuthenticationSuccessHandler;
import com.lbs.user.user.security.CustomOauth2UserInfoService;
import com.lbs.user.user.security.CustomOidcUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-23
 * 풀이방법
 **/


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOauth2UserInfoService customOauth2UserInfoService;
    private final CustomOidcUserInfoService customOidcUserInfoService;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors-> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2->oauth2
                        .userInfoEndpoint(userInfo->userInfo
                                .userService(customOauth2UserInfoService)
                                .oidcUserService(customOidcUserInfoService)
                        )
                        .successHandler(customOAuth2AuthenticationSuccessHandler)
                );

        return http.build();

    }


    // 나중에 바꿔야함
    // netty 기반의 cors는 import cors-reative 로 바꿔야함!!!!
    @Bean
    public CorsConfigurationSource  corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://evil55.shop"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
