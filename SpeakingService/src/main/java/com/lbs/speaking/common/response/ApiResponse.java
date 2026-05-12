package com.lbs.speaking.common.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int status,
        String message,
        T data,
        String errorCode
) {

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status.value(), message, data, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return success(status, status.getReasonPhrase(), data);
    }

    public static ApiResponse<Void> success(HttpStatus status, String message) {
        return success(status, message, null);
    }

    public static ApiResponse<Void> error(HttpStatus status, String message, String errorCode) {
        return new ApiResponse<>(status.value(), message, null, errorCode);
    }
}
