package com.lbs.user.card.infrastructure.repository;

import com.lbs.user.card.infrastructure.entity.DeckEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-21
 * 풀이방법
 **/

@Repository
@Qualifier("jpaDeckRepository")
public interface JpaDeckRepository extends JpaRepository<DeckEntity, Long>,DeckRepository {
 Optional<DeckEntity> findById(Long id);
 DeckEntity save(DeckEntity deckEntity);
}
