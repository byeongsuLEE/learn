package com.lbs.speaking.speaking.infrastructure.repository;

import com.lbs.speaking.speaking.infrastructure.entity.DailyQuestionEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailyQuestionJpaRepository extends JpaRepository<DailyQuestionEntity, Long> {

    Optional<DailyQuestionEntity> findByQuestionDate(LocalDate questionDate);

    @Query("select dq.question.id from DailyQuestionEntity dq")
    List<Long> findUsedQuestionIds();
}
