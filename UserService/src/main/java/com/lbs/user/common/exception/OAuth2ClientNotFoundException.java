package com.lbs.user.common.exception;

import com.lbs.user.common.response.ErrorCode;
import lombok.Getter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-05
 * 풀이방법
 **/

@Getter
public class OAuth2ClientNotFoundException extends RuntimeException{
    private final ErrorCode errorCode;

    public OAuth2ClientNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
