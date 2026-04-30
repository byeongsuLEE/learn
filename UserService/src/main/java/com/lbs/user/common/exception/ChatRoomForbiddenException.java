package com.lbs.user.common.exception;

import com.lbs.user.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class ChatRoomForbiddenException extends RuntimeException {
    private final ErrorCode errorCode;
    public ChatRoomForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
