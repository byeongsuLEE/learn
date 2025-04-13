package com.lbs.user.card.infrastructure.repository;

import com.lbs.user.card.domain.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepository {
    Optional<Deck> findById(Long id);
    Deck save(Deck deck);
    Deck update(Deck deck);
    Long delete(Long id);

    List<Deck> findBy();
}

