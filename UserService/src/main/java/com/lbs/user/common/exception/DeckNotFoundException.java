package com.lbs.user.common.exception;

import com.lbs.user.common.response.ErrorCode;
import lombok.Getter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-12
 * 풀이방법
 **/

@Getter
public class DeckNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public DeckNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
