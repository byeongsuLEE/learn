package com.lbs.user.user.security;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 풀이방법
 **/

@Component
public class GoogleUserInfoMapper implements OAuth2UserInfoMapper {
    @Override
    public boolean supports(String registrationId) {
        return "google".equals(registrationId);
    }

    @Override
    public String getId(Map<String, Object> attributes) {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName(Map<String, Object> attributes) {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail(Map<String, Object> attributes) {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl(Map<String, Object> attributes) {
        return  (String) attributes.get("picture");
    }
}
