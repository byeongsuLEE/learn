package com.lbs.user.user.security;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 풀이방법
 **/


@Component
public class GitHubUserInfoMapper implements OAuth2UserInfoMapper {
    @Override
    public boolean supports(String registrationId) {
        return "github".equals(registrationId);
    }

    @Override
    public String getId(Map<String, Object> attributes) {
        Object id = attributes.get("id");
        return id==null?null:id.toString();
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
        return (String) attributes.get("avatar_url");
    }
}
