package com.lbs.speaking.speaking.infrastructure.entity;

import com.lbs.speaking.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        uniqueConstraints = @UniqueConstraint(name = "uk_speaking_daily_question_date", columnNames = "question_date"),
        indexes = @Index(name = "idx_speaking_daily_question_date", columnList = "question_date")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuestionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_date", nullable = false)
    private LocalDate questionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    private DailyQuestionEntity(LocalDate questionDate, QuestionEntity question) {
        this.questionDate = questionDate;
        this.question = question;
    }

    public static DailyQuestionEntity create(LocalDate questionDate, QuestionEntity question) {
        return new DailyQuestionEntity(questionDate, question);
    }
}
