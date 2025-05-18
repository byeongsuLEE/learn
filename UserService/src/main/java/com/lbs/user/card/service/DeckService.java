package com.lbs.user.card.service;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.request.CreateCardRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface DeckService {
    Deck saveDeck(Deck card);
    Deck readDeck(Long id);
    Deck updateDeck(Deck card);
    Long deleteDeck(Long id);

    List<Deck> readAllDecks();
    Card createCard(Card card);
    Long deleteCard(Long id, Long cardId);
//    List<Deck> readPageDecks(PageRequest pageRequest);
public Deck updateCardCountWithTransactionTest(Long id, int newCardCount, boolean causeError);

}
