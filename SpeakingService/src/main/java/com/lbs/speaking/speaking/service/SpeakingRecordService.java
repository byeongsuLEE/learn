package com.lbs.speaking.speaking.service;

import com.lbs.speaking.common.exception.BusinessException;
import com.lbs.speaking.common.response.ErrorCode;
import com.lbs.speaking.config.MinioProperties;
import com.lbs.speaking.config.SpeakingProperties;
import com.lbs.speaking.speaking.dto.SpeakingDtos;
import com.lbs.speaking.speaking.infrastructure.entity.AnalysisEntity;
import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import com.lbs.speaking.speaking.infrastructure.repository.AnalysisJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.DailyQuestionJpaRepository;
import com.lbs.speaking.speaking.infrastructure.repository.SpeakingRecordJpaRepository;
import com.lbs.speaking.speaking.worker.AnalysisPublisher;
import com.lbs.speaking.speaking.worker.AnalysisRequestedEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpeakingRecordService {

    private final SpeakingProperties properties;
    private final MinioProperties minioProperties;
    private final TodayQuestionService todayQuestionService;
    private final DailyQuestionJpaRepository dailyQuestionRepository;
    private final SpeakingRecordJpaRepository recordRepository;
    private final AnalysisJpaRepository analysisRepository;
    private final UploadSessionService uploadSessionService;
    private final AudioStorageService audioStorageService;
    private final ObjectKeyGenerator objectKeyGenerator;
    private final ProgressService progressService;
    private final AnalysisPublisher analysisPublisher;
    private final ReanalysisLimitService reanalysisLimitService;

    @Transactional
    public SpeakingDtos.TodayQuestionResponse getTodayQuestion() {
        DailyQuestionEntity dailyQuestion = todayQuestionService.getOrCreateTodayQuestion();
        return new SpeakingDtos.TodayQuestionResponse(
                dailyQuestion.getId(),
                dailyQuestion.getQuestionDate(),
                dailyQuestion.getQuestion().getId(),
                dailyQuestion.getQuestion().getContent()
        );
    }

    @Transactional
    public SpeakingDtos.PresignedUploadResponse createUploadUrl(Long userId,
                                                                SpeakingDtos.PresignedUploadRequest request) {
        validateAudio(request.mimeType(), request.sizeBytes(), request.durationSec());
        DailyQuestionEntity dailyQuestion = todayQuestionService.getOrCreateTodayQuestion();

        String uploadId = UUID.randomUUID().toString();
        String objectKey = objectKeyGenerator.tempKey(userId, uploadId, request.mimeType());
        uploadSessionService.create(uploadId, userId, dailyQuestion.getId(), objectKey,
                request.mimeType(), request.sizeBytes(), request.durationSec());

        String uploadUrl = audioStorageService.createUploadUrl(objectKey);
        return new SpeakingDtos.PresignedUploadResponse(
                uploadId,
                uploadUrl,
                objectKey,
                minioProperties.presignedPutExpiryMinutes() * 60
        );
    }

    @Transactional
    public SpeakingDtos.RecordResponse createRecord(Long userId, SpeakingDtos.CreateRecordRequest request) {
        UploadSession session = uploadSessionService.getRequired(request.uploadId());
        validateSession(userId, request.objectKey(), session);

        DailyQuestionEntity dailyQuestion = dailyQuestionRepository.findById(session.dailyQuestionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TODAY_QUESTION_NOT_FOUND));
        if (recordRepository.existsByUserIdAndDailyQuestionIdAndDeletedAtIsNull(userId, dailyQuestion.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_DAILY_ANSWER);
        }

        audioStorageService.assertExists(session.objectKey());
        SpeakingRecordEntity record = savePendingRecord(userId, dailyQuestion, request.transcript(), session);
        progressService.setProgress(record.getId(), 55);

        String permanentObjectKey = objectKeyGenerator.permanentKey(userId, record.getId(), session.mimeType());
        audioStorageService.copy(session.objectKey(), permanentObjectKey);
        audioStorageService.delete(session.objectKey());

        record.markAnalyzing(permanentObjectKey);
        progressService.setProgress(record.getId(), 70);
        uploadSessionService.delete(session.uploadId());
        analysisPublisher.publish(new AnalysisRequestedEvent(record.getId(), userId, 1));
        return toRecordResponse(record);
    }

    private SpeakingRecordEntity savePendingRecord(Long userId, DailyQuestionEntity dailyQuestion, String transcript,
                                                   UploadSession session) {
        try {
            return recordRepository.save(SpeakingRecordEntity.createPending(
                    userId,
                    dailyQuestion,
                    session.objectKey(),
                    transcript,
                    session.mimeType(),
                    session.sizeBytes(),
                    session.durationSec()
            ));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.DUPLICATE_DAILY_ANSWER, exception);
        }
    }

    @Transactional(readOnly = true)
    public SpeakingDtos.ProgressResponse getProgress(Long userId, Long recordId) {
        SpeakingRecordEntity record = getOwnedRecord(userId, recordId);
        int progress = progressService.getProgress(record);
        return new SpeakingDtos.ProgressResponse(record.getId(), record.getStatus(), progress, progressMessage(record.getStatus(), progress));
    }

    @Transactional(readOnly = true)
    public SpeakingDtos.RecordResponse getRecord(Long userId, Long recordId) {
        return toRecordResponse(getOwnedRecord(userId, recordId));
    }

    @Transactional(readOnly = true)
    public SpeakingDtos.RecordListResponse getRecords(Long userId) {
        List<SpeakingDtos.RecordListItemResponse> records = recordRepository
                .findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(record -> new SpeakingDtos.RecordListItemResponse(
                        record.getId(),
                        record.getDailyQuestion().getQuestion().getContent(),
                        record.getStatus(),
                        record.getCreatedAt()
                ))
                .toList();
        return new SpeakingDtos.RecordListResponse(records);
    }

    @Transactional(readOnly = true)
    public SpeakingDtos.AudioUrlResponse getAudioUrl(Long userId, Long recordId) {
        SpeakingRecordEntity record = getOwnedRecord(userId, recordId);
        String url = audioStorageService.createDownloadUrl(record.getObjectKey());
        return new SpeakingDtos.AudioUrlResponse(url, minioProperties.presignedGetExpiryMinutes() * 60);
    }

    @Transactional
    public SpeakingDtos.RecordResponse reanalyze(Long userId, Long recordId) {
        SpeakingRecordEntity record = getOwnedRecord(userId, recordId);
        int attempt = reanalysisLimitService.acquire(userId, recordId, LocalDate.now(properties.zoneId()));
        record.markAnalyzing(record.getObjectKey());
        progressService.setProgress(record.getId(), 70);
        analysisPublisher.publish(new AnalysisRequestedEvent(record.getId(), userId, attempt));
        return toRecordResponse(record);
    }

    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        SpeakingRecordEntity record = getOwnedRecord(userId, recordId);
        record.softDelete();
        audioStorageService.delete(record.getObjectKey());
    }

    private SpeakingRecordEntity getOwnedRecord(Long userId, Long recordId) {
        SpeakingRecordEntity record = recordRepository.findByIdAndDeletedAtIsNull(recordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECORD_NOT_FOUND));
        if (!record.belongsTo(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return record;
    }

    private void validateAudio(String mimeType, long sizeBytes, int durationSec) {
        if (!properties.allowedMimeTypes().contains(mimeType)) {
            throw new BusinessException(ErrorCode.INVALID_AUDIO_MIME_TYPE);
        }
        if (sizeBytes > properties.maxAudioSizeBytes()) {
            throw new BusinessException(ErrorCode.AUDIO_SIZE_EXCEEDED);
        }
        if (durationSec > properties.maxDurationSec()) {
            throw new BusinessException(ErrorCode.AUDIO_DURATION_EXCEEDED);
        }
    }

    private void validateSession(Long userId, String objectKey, UploadSession session) {
        if (!session.userId().equals(userId) || !session.objectKey().equals(objectKey)) {
            throw new BusinessException(ErrorCode.INVALID_UPLOAD_SESSION);
        }
    }

    private SpeakingDtos.RecordResponse toRecordResponse(SpeakingRecordEntity record) {
        SpeakingDtos.AnalysisResponse analysis = analysisRepository.findByRecordId(record.getId())
                .map(entity -> new SpeakingDtos.AnalysisResponse(
                        entity.getImprovedText(),
                        entity.getIssuesJson(),
                        entity.getRenderBlocksJson()
                ))
                .orElse(null);

        return new SpeakingDtos.RecordResponse(
                record.getId(),
                record.getDailyQuestion().getId(),
                record.getDailyQuestion().getQuestion().getContent(),
                record.getOriginalText(),
                record.getStatus(),
                record.getFailureReason(),
                record.getCreatedAt(),
                analysis
        );
    }

    private String progressMessage(SpeakingRecordStatus status, int progress) {
        if (status == SpeakingRecordStatus.FAILED) {
            return "Analysis failed.";
        }
        if (progress >= 100) {
            return "Completed.";
        }
        if (progress >= 90) {
            return "Saving analysis.";
        }
        if (progress >= 70) {
            return "Analyzing answer.";
        }
        if (progress >= 55) {
            return "Record saved.";
        }
        return "Preparing.";
    }

}
