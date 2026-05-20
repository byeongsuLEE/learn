package com.lbs.speaking.speaking.infrastructure.repository;

import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordEntity;
import com.lbs.speaking.speaking.infrastructure.entity.SpeakingRecordStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakingRecordJpaRepository extends JpaRepository<SpeakingRecordEntity, Long> {

    boolean existsByUserIdAndDailyQuestionIdAndDeletedAtIsNull(Long userId, Long dailyQuestionId);

    Optional<SpeakingRecordEntity> findByUserIdAndDailyQuestionIdAndDeletedAtIsNull(Long userId, Long dailyQuestionId);

    Optional<SpeakingRecordEntity> findByIdAndDeletedAtIsNull(Long id);

    List<SpeakingRecordEntity> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    List<SpeakingRecordEntity> findTop100ByStatusInAndUpdatedAtBeforeAndDeletedAtIsNullOrderByUpdatedAtAsc(
            Collection<SpeakingRecordStatus> statuses,
            LocalDateTime updatedAt
    );
}
