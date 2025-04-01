package com.lbs.user.common.exception;

import com.lbs.user.common.response.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 작성자  : lb
 * 날짜    : 2025-04-01
 * 풀이방법
 **/

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
