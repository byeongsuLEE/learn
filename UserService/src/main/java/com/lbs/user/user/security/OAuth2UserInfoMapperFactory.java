package com.lbs.user.user.security;

import com.lbs.user.common.exception.DeckNotFoundException;
import com.lbs.user.common.exception.OAuth2ClientNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 설명 :  * OAuth2 사용자 정보 매퍼를 제공하는 팩토리 클래스
 **/

@Component
@RequiredArgsConstructor
public class OAuth2UserInfoMapperFactory {
    private final List<OAuth2UserInfoMapper> mappers;

    public OAuth2UserInfoMapper getOAuth2UserInfoMapper(String registrationId) {

        return mappers.stream()
                .filter(mapper-> mapper.supports(registrationId))
                .findFirst()
                .orElseThrow(()-> new OAuth2ClientNotFoundException(ErrorCode.OAUTH2CLIENT_NOT_FOUND));
    }
}
