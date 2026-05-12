package com.lbs.speaking.speaking.infrastructure.repository;

import com.lbs.speaking.speaking.infrastructure.entity.QuestionEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByActiveTrueAndIdNotIn(Collection<Long> excludedIds);

    List<QuestionEntity> findByActiveTrue();
}
