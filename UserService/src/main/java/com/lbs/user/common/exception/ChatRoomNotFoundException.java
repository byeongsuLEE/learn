package com.lbs.user.common.exception;

import com.lbs.user.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class ChatRoomNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    public ChatRoomNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
