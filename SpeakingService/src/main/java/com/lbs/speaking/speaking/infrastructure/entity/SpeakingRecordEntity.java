package com.lbs.speaking.speaking.infrastructure.entity;

import com.lbs.speaking.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "speaking_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_speaking_record_user_daily_question",
                columnNames = {"user_id", "daily_question_id"}
        ),
        indexes = {
                @Index(name = "idx_speaking_record_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_speaking_record_status_updated_at", columnList = "status, updated_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpeakingRecordEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_question_id")
    private DailyQuestionEntity dailyQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 30)
    private SpeakingRecordType recordType;

    @Column(name = "prompt_text", columnDefinition = "TEXT")
    private String promptText;

    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "duration_sec", nullable = false)
    private int durationSec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SpeakingRecordStatus status;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private SpeakingRecordEntity(Long userId, DailyQuestionEntity dailyQuestion, String objectKey,
                                 String originalText, String mimeType, long sizeBytes, int durationSec) {
        this.userId = userId;
        this.dailyQuestion = dailyQuestion;
        this.recordType = SpeakingRecordType.QUESTION_ANALYSIS;
        this.objectKey = objectKey;
        this.originalText = originalText;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.durationSec = durationSec;
        this.status = SpeakingRecordStatus.PENDING_ANALYSIS;
    }

    public static SpeakingRecordEntity createPending(Long userId, DailyQuestionEntity dailyQuestion, String objectKey,
                                                     String originalText, String mimeType, long sizeBytes,
                                                     int durationSec) {
        return new SpeakingRecordEntity(userId, dailyQuestion, objectKey, originalText, mimeType, sizeBytes, durationSec);
    }

    public static SpeakingRecordEntity createRecordedCustomText(Long userId, String promptText, String objectKey,
                                                                String originalText, String mimeType, long sizeBytes,
                                                                int durationSec) {
        SpeakingRecordEntity record = new SpeakingRecordEntity(userId, null, objectKey, originalText, mimeType, sizeBytes, durationSec);
        record.recordType = SpeakingRecordType.CUSTOM_TEXT;
        record.promptText = promptText;
        record.status = SpeakingRecordStatus.RECORDED;
        return record;
    }

    public void markAnalyzing(String permanentObjectKey) {
        this.objectKey = permanentObjectKey;
        this.status = SpeakingRecordStatus.ANALYZING;
        this.failureReason = null;
    }

    public void markCompleted() {
        this.status = SpeakingRecordStatus.COMPLETED;
        this.failureReason = null;
    }

    public void markRecorded(String permanentObjectKey) {
        this.objectKey = permanentObjectKey;
        this.status = SpeakingRecordStatus.RECORDED;
        this.failureReason = null;
    }

    public void markFailed(String failureReason) {
        this.status = SpeakingRecordStatus.FAILED;
        this.failureReason = failureReason;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isQuestionAnalysis() {
        return recordType == SpeakingRecordType.QUESTION_ANALYSIS && dailyQuestion != null;
    }
}
