package com.lbs.speaking.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access is denied."),
    TODAY_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Today question was not found."),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "Speaking record was not found."),
    UPLOAD_SESSION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Upload session is missing or expired."),
    INVALID_UPLOAD_SESSION(HttpStatus.BAD_REQUEST, "Upload session does not match the request."),
    INVALID_AUDIO_MIME_TYPE(HttpStatus.BAD_REQUEST, "Audio mime type is not allowed."),
    AUDIO_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "Audio size exceeds the allowed limit."),
    AUDIO_DURATION_EXCEEDED(HttpStatus.BAD_REQUEST, "Audio duration exceeds the allowed limit."),
    AUDIO_OBJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "Uploaded audio object was not found."),
    DUPLICATE_DAILY_ANSWER(HttpStatus.CONFLICT, "Daily question was already answered."),
    ANALYSIS_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Daily analysis limit exceeded."),
    LLM_RESPONSE_PARSE_FAILED(HttpStatus.BAD_GATEWAY, "LLM response could not be parsed."),
    LLM_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "LLM request failed."),
    STORAGE_OPERATION_FAILED(HttpStatus.BAD_GATEWAY, "Object storage operation failed."),
    MESSAGE_PUBLISH_FAILED(HttpStatus.BAD_GATEWAY, "Analysis message publish failed.");

    private final HttpStatus httpStatus;
    private final String message;
}
