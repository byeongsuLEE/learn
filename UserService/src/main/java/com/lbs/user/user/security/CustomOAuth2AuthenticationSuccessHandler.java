package com.lbs.user.user.security;

import com.lbs.user.user.domain.User;
import com.lbs.user.user.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.oauth2.redirectUri}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        User user = null;
        if (principal instanceof CustomOidcUser) {
            user = ((CustomOidcUser) principal).getUser();
        } else if (principal instanceof CustomOauth2User) {
            user = ((CustomOauth2User) principal).getUser();
        }

        if (user == null) {
            response.sendRedirect(frontendRedirectUrl + "/auth/login?error=user_not_found");
            return;
        }

        var authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        String primaryRole = user.getRoles().isEmpty() ? "PARENT" : user.getRoles().get(0);

        String token = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getName(), primaryRole, authorities);

        // JWT를 HttpOnly 쿠키로 전달 (60초 유효 — 프론트 콜백 페이지가 교환 후 즉시 삭제)
        ResponseCookie cookie = ResponseCookie.from("oauth_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofSeconds(60))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("OAuth2 login success — userId: {}, email: {}", user.getId(), user.getEmail());
        response.sendRedirect(frontendRedirectUrl + "/auth/callback");
    }
}
