package com.lbs.user.user.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 풀이방법
 **/

@Service
@Slf4j
public class CustomOauth2UserInfoService extends DefaultOAuth2UserService  {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth2 로그인 요청 : {}", userRequest.getClientRegistration().getClientId());
        log.info("OAuth 로그인 : " + oAuth2User.getAttributes());
        Map<String, Object> attributes = oAuth2User.getAttributes();

        return oAuth2User;
    }


}
