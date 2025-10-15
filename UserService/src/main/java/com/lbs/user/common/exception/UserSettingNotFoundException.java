package com.lbs.user.common.exception;

import com.lbs.user.common.response.ErrorCode;
import lombok.Getter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-02
 * 풀이방법
 **/

@Getter
public class UserSettingNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public UserSettingNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
