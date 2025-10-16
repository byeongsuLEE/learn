package com.lbs.user.common.response;

import com.google.api.Http;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    FRINED_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 친구 요청을 찾을 수 없습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저가 없습니다"),
    USER_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저 세팅 정보가 없습니다"),
    DECK_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 낱말카드 묶음이 없습니다"),
    Exception_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Exception 에러 입니다."),
    RuntimeException_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Runtime 에러 입니다."),
    OAUTH2CLIENT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "지원하지 않는 OAuth2 제공자입니다"),

    // JWT 관련 에러
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다"),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다"),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 JWT 오류가 발생했습니다"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다"),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "사용이 금지된 토큰입니다");

    private HttpStatus status;
    private String message;
}
