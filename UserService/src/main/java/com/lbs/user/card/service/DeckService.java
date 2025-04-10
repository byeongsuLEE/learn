package com.lbs.user.card.service;

import com.lbs.user.card.domain.Deck;

import javax.smartcardio.Card;

public interface DeckService {
    Deck createCard(Deck card);
    Deck readCard(Long id);
    Deck updateCard(Deck card);
    Deck deleteCard(Long id);
}
