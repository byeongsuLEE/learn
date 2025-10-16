package com.lbs.user.common.exception;

import ch.qos.logback.core.spi.ErrorCodes;
import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 작성자  : lb
 * 날짜    : 2025-04-01
 * 풀이방법
 **/


@Slf4j
@RestControllerAdvice
public class AllExceptionHandler {

    @ExceptionHandler(FriendRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleFriendRequestException(FriendRequestException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(), errorCode));

    }

    @ExceptionHandler(UserSettingNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserSettingNotFoundException(UserSettingNotFoundException userSettingNotFoundException) {
        ErrorCode errorCode = userSettingNotFoundException.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(), errorCode));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        ErrorCode errorCode = userNotFoundException.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(),errorCode));
    }

    @ExceptionHandler(DeckNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleDeckNotFoundException(DeckNotFoundException deckNotFoundException) {
        ErrorCode errorCode = deckNotFoundException.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(),errorCode));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleException(RuntimeException runtimeException) {
        ErrorCode errorCode = ErrorCode.RuntimeException_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(),errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception exception) {
        ErrorCode errorCode = ErrorCode.Exception_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorCode.getStatus(), errorCode.getMessage(),errorCode));
    }
}
