package com.lbs.speaking.speaking.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbs.speaking.auth.JwtTokenProvider;
import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.speaking.dto.SpeakingDtos;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordType;
import com.lbs.speaking.speaking.service.SpeakingRecordService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SpeakingController.class)
class SpeakingControllerTest {

    private static final String AUTHORIZATION = "Bearer token";
    private static final Long USER_ID = 7L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpeakingRecordService speakingRecordService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("GET /speaking/today returns today's question without authentication")
    void getTodayQuestion() throws Exception {
        given(speakingRecordService.getTodayQuestion()).willReturn(new SpeakingDtos.TodayQuestionResponse(
                10L,
                LocalDate.of(2026, 5, 12),
                20L,
                "What did you learn today?"
        ));

        mockMvc.perform(get("/speaking/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.dailyQuestionId").value(10))
                .andExpect(jsonPath("$.data.questionId").value(20))
                .andExpect(jsonPath("$.data.content").value("What did you learn today?"));
    }

    @Test
    @DisplayName("GET /speaking/interview/today returns today's interview question")
    void getInterviewQuestion() throws Exception {
        given(speakingRecordService.getInterviewQuestion()).willReturn(new SpeakingDtos.TodayQuestionResponse(
                11L,
                LocalDate.of(2026, 5, 12),
                21L,
                "Tell me about yourself and your background.",
                com.lbs.speaking.speaking.infrastructure.entity.SpeakingQuestionType.INTERVIEW
        ));

        mockMvc.perform(get("/speaking/interview/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyQuestionId").value(11))
                .andExpect(jsonPath("$.data.questionType").value("INTERVIEW"))
                .andExpect(jsonPath("$.data.content").value("Tell me about yourself and your background."));
    }

    @Test
    @DisplayName("protected APIs return 401 when Authorization is missing")
    void missingAuthorization() throws Exception {
        given(jwtTokenProvider.resolveUserId(null)).willThrow(new BusinessException(ErrorCode.UNAUTHORIZED));

        mockMvc.perform(post("/speaking/uploads/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.PresignedUploadRequest(
                                "audio/webm",
                                1024,
                                30
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("validation errors return 400 with VALIDATION_ERROR")
    void requestValidation() throws Exception {
        mockMvc.perform(post("/speaking/records")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "uploadId": "",
                                  "objectKey": "speaking/temp/7/upload.webm",
                                  "transcript": "hello"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("audio validation domain errors keep their API error code")
    void invalidAudioMimeType() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createUploadUrl(eq(USER_ID), any()))
                .willThrow(new BusinessException(ErrorCode.INVALID_AUDIO_MIME_TYPE));

        mockMvc.perform(post("/speaking/uploads/presigned-url")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.PresignedUploadRequest(
                                "video/mp4",
                                1024,
                                30
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_AUDIO_MIME_TYPE"));
    }

    @Test
    @DisplayName("presigned upload response exposes upload metadata")
    void createPresignedUploadUrl() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createUploadUrl(eq(USER_ID), any()))
                .willReturn(new SpeakingDtos.PresignedUploadResponse(
                        "upload-1",
                        "https://evil55.cloud/speaking-audio/speaking/temp/7/upload-1.webm",
                        "speaking/temp/7/upload-1.webm",
                        600
                ));

        mockMvc.perform(post("/speaking/uploads/presigned-url")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.PresignedUploadRequest(
                                "audio/webm",
                                1024,
                                30
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uploadId").value("upload-1"))
                .andExpect(jsonPath("$.data.uploadUrl", containsString("https://evil55.cloud")))
                .andExpect(jsonPath("$.data.objectKey").value("speaking/temp/7/upload-1.webm"))
                .andExpect(jsonPath("$.data.expiresInSec").value(600));
    }

    @Test
    @DisplayName("custom presigned upload response does not require a daily question")
    void createCustomPresignedUploadUrl() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createCustomUploadUrl(eq(USER_ID), any()))
                .willReturn(new SpeakingDtos.PresignedUploadResponse(
                        "upload-custom-1",
                        "https://evil55.cloud/speaking-audio/speaking/temp/7/upload-custom-1.webm",
                        "speaking/temp/7/upload-custom-1.webm",
                        600
                ));

        mockMvc.perform(post("/speaking/custom/uploads/presigned-url")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.PresignedUploadRequest(
                                "audio/webm",
                                1024,
                                30
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uploadId").value("upload-custom-1"))
                .andExpect(jsonPath("$.data.objectKey").value("speaking/temp/7/upload-custom-1.webm"));
    }

    @Test
    @DisplayName("record creation returns 201")
    void createRecord() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createRecord(eq(USER_ID), any()))
                .willReturn(recordResponse(SpeakingRecordStatus.ANALYZING));

        mockMvc.perform(post("/speaking/records")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.CreateRecordRequest(
                                "upload-1",
                                "speaking/temp/7/upload-1.webm",
                                "I learned queue based analysis today."
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Record created."))
                .andExpect(jsonPath("$.data.status").value("ANALYZING"));
    }

    @Test
    @DisplayName("custom record creation returns a recorded record without analysis")
    void createCustomRecord() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createCustomRecord(eq(USER_ID), any()))
                .willReturn(new SpeakingDtos.RecordResponse(
                        2L,
                        null,
                        "내가 쓴 발표문",
                        SpeakingRecordType.CUSTOM_TEXT,
                        "내가 쓴 발표문",
                        SpeakingRecordStatus.RECORDED,
                        null,
                        LocalDateTime.of(2026, 5, 12, 10, 0),
                        null
                ));

        mockMvc.perform(post("/speaking/custom/records")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.CreateCustomRecordRequest(
                                "upload-custom-1",
                                "speaking/temp/7/upload-custom-1.webm",
                                "내가 쓴 발표문",
                                "내가 쓴 발표문"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.dailyQuestionId").doesNotExist())
                .andExpect(jsonPath("$.data.recordType").value("CUSTOM_TEXT"))
                .andExpect(jsonPath("$.data.status").value("RECORDED"))
                .andExpect(jsonPath("$.data.analysis").doesNotExist());
    }

    @Test
    @DisplayName("duplicate daily answer returns 409")
    void duplicateDailyAnswer() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createRecord(eq(USER_ID), any()))
                .willThrow(new BusinessException(ErrorCode.DUPLICATE_DAILY_ANSWER));

        mockMvc.perform(post("/speaking/records")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.CreateRecordRequest(
                                "upload-1",
                                "speaking/temp/7/upload-1.webm",
                                "I already answered."
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_DAILY_ANSWER"));
    }

    @Test
    @DisplayName("daily analysis limit returns 429")
    void analysisLimitExceeded() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.createRecord(eq(USER_ID), any()))
                .willThrow(new BusinessException(ErrorCode.ANALYSIS_LIMIT_EXCEEDED));

        mockMvc.perform(post("/speaking/records")
                        .header("Authorization", AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpeakingDtos.CreateRecordRequest(
                                "upload-1",
                                "speaking/temp/7/upload-1.webm",
                                "I already analyzed five times."
                        ))))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errorCode").value("ANALYSIS_LIMIT_EXCEEDED"));
    }

    @Test
    @DisplayName("other user's record access returns 403")
    void forbiddenRecordAccess() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.getRecord(USER_ID, 99L))
                .willThrow(new BusinessException(ErrorCode.FORBIDDEN));

        mockMvc.perform(get("/speaking/records/99")
                        .header("Authorization", AUTHORIZATION))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("progress response returns current status and percentage")
    void getProgress() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.getProgress(USER_ID, 1L))
                .willReturn(new SpeakingDtos.ProgressResponse(1L, SpeakingRecordStatus.ANALYZING, 70, "Analyzing answer."));

        mockMvc.perform(get("/speaking/records/1/progress")
                        .header("Authorization", AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recordId").value(1))
                .andExpect(jsonPath("$.data.status").value("ANALYZING"))
                .andExpect(jsonPath("$.data.progress").value(70));
    }

    @Test
    @DisplayName("audio endpoint returns a fresh presigned download URL")
    void getAudioUrl() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);
        given(speakingRecordService.getAudioUrl(USER_ID, 1L))
                .willReturn(new SpeakingDtos.AudioUrlResponse("https://evil55.cloud/speaking-audio/audio.webm", 600));

        mockMvc.perform(get("/speaking/records/1/audio")
                        .header("Authorization", AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.audioUrl", containsString("https://evil55.cloud")))
                .andExpect(jsonPath("$.data.expiresInSec").value(600));
    }

    @Test
    @DisplayName("delete soft-deletes the record")
    void deleteRecord() throws Exception {
        given(jwtTokenProvider.resolveUserId(AUTHORIZATION)).willReturn(USER_ID);

        mockMvc.perform(delete("/speaking/records/1")
                        .header("Authorization", AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record deleted."));

        verify(speakingRecordService).deleteRecord(USER_ID, 1L);
    }

    private SpeakingDtos.RecordResponse recordResponse(SpeakingRecordStatus status) {
        return new SpeakingDtos.RecordResponse(
                1L,
                10L,
                "What did you learn today?",
                SpeakingRecordType.QUESTION_ANALYSIS,
                "I learned queue based analysis today.",
                status,
                null,
                LocalDateTime.of(2026, 5, 12, 10, 0),
                null
        );
    }
}
