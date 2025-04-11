package com.lbs.user.card.service;

import com.lbs.user.card.domain.Deck;

public interface DeckService {
    Deck createDeck(Deck card);
    Deck readDeck(Long id);
    Deck updateDeck(Deck card);
    Deck deleteDeck(Long id);
}
