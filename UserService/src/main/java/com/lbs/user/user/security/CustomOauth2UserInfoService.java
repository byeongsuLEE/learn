package com.lbs.user.user.security;

import com.lbs.user.user.domain.User;
import com.lbs.user.user.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 풀이방법
 **/

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserInfoService extends DefaultOAuth2UserService  {
    private final OAuth2UserInfoMapperFactory oAuth2UserInfoMapperFactory;
    private final UserService userService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String clientId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 요청 : {}", clientId);
        log.info("OAuth 로그인 : " + oAuth2User.getAttributes());
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfoMapper clientMapper = oAuth2UserInfoMapperFactory.getOAuth2UserInfoMapper(clientId);
        String id = clientMapper.getId(attributes);
        String email = clientMapper.getEmail(attributes);
        String name = clientMapper.getName(attributes);
        String imageUrl = clientMapper.getImageUrl(attributes);

        //email로 userEntity 가져온다.-
        User user = userService.readUserByEmail(email);
        if (user == null) {
            user  = User.builder()
                    .imageUrl(imageUrl)
                    .email(email)
                    .name(name)
                    .provider(clientId)
                    .providerId(id)
                    .roles(List.of("ROLE_USER"))
                    .build();
            userService.joinUser(user);
        }
        // 해당 정보를 가지고
        return new CustomOauth2User(oAuth2User,user);
    }


}
