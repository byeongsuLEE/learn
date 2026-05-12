package com.lbs.speaking.auth;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(JwtProperties properties) {
        byte[] keyBytes = decodeSecret(properties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Long resolveUserId(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Claims claims = parseClaims(authorizationHeader.substring(7));
        Object userId = claims.get("userId");
        if (userId instanceof Number number) {
            return number.longValue();
        }
        if (userId instanceof String value && StringUtils.hasText(value)) {
            return Long.parseLong(value);
        }
        String subject = claims.getSubject();
        if (StringUtils.hasText(subject)) {
            return Long.parseLong(subject);
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception exception) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, exception);
        }
    }

    private byte[] decodeSecret(String secret) {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ignored) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}
