package com.lbs.user.user.security;

import com.lbs.user.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 풀이방법
 **/


@RequiredArgsConstructor
@Getter
public class CustomOidcUser implements OidcUser {
    private final OidcUser oidcUser;
    private final User user;

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }



    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 역할을 Spring Security 권한으로 변환
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return user.getEmail() == null ? oidcUser.getName() : user.getEmail();
    }



}
