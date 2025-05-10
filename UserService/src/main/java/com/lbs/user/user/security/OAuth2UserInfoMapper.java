package com.lbs.user.user.security;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Map;

/**
 * oauth2 client 사용자 정보를 추출하기 위한 인터페이스
 * @ 작성자   : lb
 * @ 작성일   : 2025-05-05
 * @ 설명     :  if(github, google) 등 if문 쓰기 위해서 추상화

 */
public interface OAuth2UserInfoMapper {
    boolean supports(String registrationId);
    String getId(Map<String, Object> attributes);
    String getName(Map<String, Object> attributes);
    String getEmail(Map<String, Object> attributes);
    String getImageUrl(Map<String, Object> attributes);
}
