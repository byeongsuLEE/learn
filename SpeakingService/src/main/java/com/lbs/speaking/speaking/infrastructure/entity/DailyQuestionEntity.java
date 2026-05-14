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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "speaking_daily_questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_speaking_daily_question_date_type",
                columnNames = {"question_date", "question_type"}
        ),
        indexes = {
                @Index(name = "idx_speaking_daily_question_date", columnList = "question_date"),
                @Index(name = "idx_speaking_daily_question_date_type", columnList = "question_date, question_type")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuestionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_date", nullable = false)
    private LocalDate questionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    private SpeakingQuestionType questionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    private DailyQuestionEntity(LocalDate questionDate, SpeakingQuestionType questionType, QuestionEntity question) {
        this.questionDate = questionDate;
        this.questionType = questionType;
        this.question = question;
    }

    public static DailyQuestionEntity create(LocalDate questionDate, QuestionEntity question) {
        return create(questionDate, SpeakingQuestionType.DAILY, question);
    }

    public static DailyQuestionEntity create(LocalDate questionDate, SpeakingQuestionType questionType, QuestionEntity question) {
        return new DailyQuestionEntity(questionDate, questionType, question);
    }
}
