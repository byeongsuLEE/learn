package com.lbs.user.user.jwt;

import com.lbs.user.common.response.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-08
 * 풀이방법
 **/


@Component
@Slf4j
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // Base64 디코딩을 통한 키 초기화
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(String email, String name, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + jwtProperties.getAccessExpirationTime());

        String authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                .setSubject(email)
                .claim("auth", authoritiesString)
                .claim("name", name)
                .setIssuedAt(now) // 발생 시간
                .setExpiration(expireTime) // 만료 일
                .signWith(key)   // 서명 키
                .compact();

        return accessToken;
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + jwtProperties.getRefreshExpirationTime());

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now) // 발생 시간
                .setExpiration(expireTime) // 만료 일
                .signWith(key)   // 서명 키
                .compact();

        return refreshToken;
    }

    /**
     * AccessToken 복호화
     * ParseClaimsJws 메서드가 JWT 토큰의 검증과 파싱을 모두 수행
     * @param accessToken
     * @return Claims
     */
    public Claims parseClaims(String accessToken) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        }catch (JwtException e) {
            throw new JwtException(ErrorCode.EXPIRED_TOKEN.getMessage());
        }
    }

    public String getUserName(String token) {
        Claims claims = parseClaims(token);
        return claims.get("name", String.class); // JWT 토큰에서 사용자 유형을 추출
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String userEmail = claims.getSubject();
        String authorities = claims.get("auth", String.class);
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            authorityList = Arrays.stream(authorities.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        User user = new User(userEmail, null, authorityList);
        return new UsernamePasswordAuthenticationToken(user, null, authorityList);

    }


    /**
     * 요청으로 토큰 가져오기
     * @ 작성자   : lbs
     * @ 작성일   : 2025-05-10
     * @ 설명     : 토큰 가져오기
     * @return  토큰 or null
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    /**
     * 토큰 검사하기
     * @ 작성자   : lbs
     * @ 작성일   : 2025-05-11
     * @ 설명     : 토큰 검사 후 에러 발생시 jwtExceptionFilter로 위임
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            throw new JwtException(ErrorCode.INVALID_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            throw new JwtException(ErrorCode.EXPIRED_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new JwtException(ErrorCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JwtException(ErrorCode.INVALID_TOKEN.getMessage());
        } catch (Exception e) {
            throw new JwtException(ErrorCode.UNKNOWN_ERROR.getMessage());
        }
    }

}





