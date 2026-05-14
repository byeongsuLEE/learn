package com.lbs.speaking.speaking.controller;

import com.lbs.speaking.auth.JwtTokenProvider;
import com.lbs.speaking.common.response.ApiResponse;
import com.lbs.speaking.speaking.dto.SpeakingDtos;
import com.lbs.speaking.speaking.service.SpeakingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/speaking")
public class SpeakingController {

    private final SpeakingRecordService speakingRecordService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<SpeakingDtos.TodayQuestionResponse>> getTodayQuestion() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.getTodayQuestion()));
    }

    @GetMapping("/interview/today")
    public ResponseEntity<ApiResponse<SpeakingDtos.TodayQuestionResponse>> getInterviewQuestion() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.getInterviewQuestion()));
    }

    @PostMapping("/uploads/presigned-url")
    public ResponseEntity<ApiResponse<SpeakingDtos.PresignedUploadResponse>> createUploadUrl(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody SpeakingDtos.PresignedUploadRequest request
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.createUploadUrl(userId, request)));
    }

    @PostMapping("/custom/uploads/presigned-url")
    public ResponseEntity<ApiResponse<SpeakingDtos.PresignedUploadResponse>> createCustomUploadUrl(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody SpeakingDtos.PresignedUploadRequest request
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.createCustomUploadUrl(userId, request)));
    }

    @PostMapping("/records")
    public ResponseEntity<ApiResponse<SpeakingDtos.RecordResponse>> createRecord(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody SpeakingDtos.CreateRecordRequest request
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Record created.", speakingRecordService.createRecord(userId, request)));
    }

    @PostMapping("/custom/records")
    public ResponseEntity<ApiResponse<SpeakingDtos.RecordResponse>> createCustomRecord(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody SpeakingDtos.CreateCustomRecordRequest request
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "Record created.", speakingRecordService.createCustomRecord(userId, request)));
    }

    @GetMapping("/records/{recordId}/progress")
    public ResponseEntity<ApiResponse<SpeakingDtos.ProgressResponse>> getProgress(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long recordId
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.getProgress(userId, recordId)));
    }

    @GetMapping("/records/{recordId}")
    public ResponseEntity<ApiResponse<SpeakingDtos.RecordResponse>> getRecord(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long recordId
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.getRecord(userId, recordId)));
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<SpeakingDtos.RecordListResponse>> getRecords(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.getRecords(userId)));
    }

    @GetMapping("/records/{recordId}/audio")
    public ResponseEntity<ApiResponse<SpeakingDtos.AudioUrlResponse>> getAudioUrl(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long recordId
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.getAudioUrl(userId, recordId)));
    }

    @PostMapping("/records/{recordId}/reanalyze")
    public ResponseEntity<ApiResponse<SpeakingDtos.RecordResponse>> reanalyze(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long recordId,
            @RequestBody(required = false) SpeakingDtos.ReanalyzeRequest request
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, speakingRecordService.reanalyze(userId, recordId)));
    }

    @DeleteMapping("/records/{recordId}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long recordId
    ) {
        Long userId = jwtTokenProvider.resolveUserId(authorization);
        speakingRecordService.deleteRecord(userId, recordId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "Record deleted."));
    }
}
