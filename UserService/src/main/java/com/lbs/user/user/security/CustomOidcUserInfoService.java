package com.lbs.user.user.security;

import com.lbs.user.user.domain.User;
import com.lbs.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
@Slf4j
@RequiredArgsConstructor
public class CustomOidcUserInfoService extends OidcUserService {

    private final OAuth2UserInfoMapperFactory oAuth2UserInfoMapperFactory;
    private final UserService userService;

    @Override
    @Transactional
    public OidcUser  loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser  = super.loadUser(userRequest);

        String clientId = userRequest.getClientRegistration().getClientId();
        log.info("OIDC  로그인 요청 : {}", clientId);
        log.info("OIDC  로그인 : " + oidcUser.getAttributes());
        // Client(Github,Google)에 맞는 정보들 가져오기
        OAuth2UserInfoMapper clientMapper = oAuth2UserInfoMapperFactory.getOAuth2UserInfoMapper(clientId);
        Map<String, Object> attributes = oidcUser.getAttributes();
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
        return new CustomOidcUser(oidcUser,user);

    }


}
