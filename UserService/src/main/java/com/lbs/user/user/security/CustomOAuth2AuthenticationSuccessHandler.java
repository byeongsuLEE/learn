package com.lbs.user.user.security;

import com.lbs.user.user.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.URIParameter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-06
 * 풀이방법
 **/


@Slf4j
@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    //jwt, redis 주입 코드
    // priavet final jwt
    // pirvate final redis ~~

    @Value("${app.oauth2.redirectUri}")
    private  String FRONTEND_REDIRECT_URL;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
            , HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();
        User user = null;
        if(principal instanceof CustomOidcUser) {
            user = ((CustomOidcUser) principal).getUser();
        }

        if (principal instanceof CustomOauth2User) {
            user = ((CustomOauth2User) principal).getUser();
        }

        String redirectUrl = UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT_URL)
                .queryParam("token" , "you have to set token")
                        .build().toUriString();

        //jwt를 userinfo로 만듬
        log.info("target videoURL : {} , user id : {}, user name : {} , user email : {} ",redirectUrl, user.getId().toString(),user.getName(),user.getEmail());

//        // 방법 1: 헤더 직접 설정
//        response.setStatus(HttpServletResponse.SC_FOUND); // 302 상태 코드
//        response.setHeader("Location", redirectUrl);
//        response.sendRedirect(redirectUrl);
        response.sendRedirect(redirectUrl);

    }
}
