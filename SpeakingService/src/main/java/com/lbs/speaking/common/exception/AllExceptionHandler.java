package com.lbs.speaking.common.exception;

import com.lbs.speaking.common.response.ApiResponse;
import com.lbs.speaking.common.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AllExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getHttpStatus(), errorCode.getMessage(), errorCode.name()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(Exception exception) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid request.", "VALIDATION_ERROR"));
    }
}
