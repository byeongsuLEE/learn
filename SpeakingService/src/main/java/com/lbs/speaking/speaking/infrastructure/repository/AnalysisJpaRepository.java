package com.lbs.speaking.speaking.infrastructure.repository;

import com.lbs.speaking.speaking.infrastructure.entity.AnalysisEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisJpaRepository extends JpaRepository<AnalysisEntity, Long> {

    Optional<AnalysisEntity> findByRecordId(Long recordId);

    boolean existsByRecordId(Long recordId);
}
