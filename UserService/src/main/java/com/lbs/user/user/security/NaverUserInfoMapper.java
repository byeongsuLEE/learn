package com.lbs.user.user.security;

import com.lbs.user.user.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-13
 * 풀이방법
 **/


@Component
public class NaverUserInfoMapper implements OAuth2UserInfoMapper {
    @Override
    public boolean supports(String registrationId) {
        return "naver".equals(registrationId);
    }
    @Override
    public String getId(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("id");
    }

    @Override
    public String getEmail(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("email");
    }

    @Override
    public String getName(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("name");
    }

    @Override
    public String getImageUrl(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("profile_image");
    }
}
