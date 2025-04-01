package com.lbs.user.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 유저가 없습니다"),
    Exception_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Exception 에러 입니다."),
    RuntimeException_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Runtime 에러 입니다.");
    private HttpStatus status;
    private String message;
}
