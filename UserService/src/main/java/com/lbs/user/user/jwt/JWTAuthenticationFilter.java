package com.lbs.user.user.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청 시 jwt 토큰 검사 필터
 * 작성자  : lbs
 * 날짜    : 2025-05-11
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //validateToken 에러 시 jwtException filter 실행 됨

            //나중에 redis에 있는  블랙 리스트도 추가해주자!
//            if (redisUtil.getData(token) != null) {
//                jwtExceptionHandler(response, ErrorCode.BLACK_LIST_TOKEN);
//                return;
//            }
            //로그인 같은 jwt token이 없는경우에는 어떻게하지?
            // login 페이지로 이동시켜야할 꺼 같다. 나중에 시켜보자.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);


    }

}

