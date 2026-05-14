package com.lbs.speaking.speaking.infrastructure.repository;

import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingQuestionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyQuestionJpaRepository extends JpaRepository<DailyQuestionEntity, Long> {

    Optional<DailyQuestionEntity> findByQuestionDate(LocalDate questionDate);

    Optional<DailyQuestionEntity> findByQuestionDateAndQuestionType(LocalDate questionDate, SpeakingQuestionType questionType);

    @Query("select dq.question.id from DailyQuestionEntity dq where dq.questionType = :questionType")
    List<Long> findUsedQuestionIds(@Param("questionType") SpeakingQuestionType questionType);
}
