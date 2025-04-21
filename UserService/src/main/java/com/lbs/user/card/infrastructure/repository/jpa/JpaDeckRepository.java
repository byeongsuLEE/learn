package com.lbs.user.card.infrastructure.repository.jpa;

import com.lbs.user.card.infrastructure.entity.CardEntity;
import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-21
 * 풀이방법
 **/

@Repository
@Qualifier("jpaDeckRepository")
public interface JpaDeckRepository extends JpaRepository<DeckEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update DeckEntity d set d.description = :desc")
    int bulkUpdateDescription(@Param("desc") String desc);

    @EntityGraph(attributePaths = {"cards"})
    List<DeckEntity> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<DeckEntity> findAllBy();


}
