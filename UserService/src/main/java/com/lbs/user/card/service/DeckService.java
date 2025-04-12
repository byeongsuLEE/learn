package com.lbs.user.card.service;

import com.lbs.user.card.domain.Deck;

import java.util.List;

public interface DeckService {
    Deck saveDeck(Deck card);
    Deck readDeck(Long id);
    Deck updateDeck(Deck card);
    Deck deleteDeck(Long id);

    List<Deck> readAllDecks();
}
