package com.lbs.user.card.infrastructure.repository;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.infrastructure.entity.DeckEntity;

import java.util.List;
import java.util.Optional;

public interface DeckRepository {
    Optional<Deck> findById(Long id);
    Deck save(Deck deck);
    Deck update(Deck deck);
    Deck delete(Deck deck);

    List<Deck> findBy();
}

