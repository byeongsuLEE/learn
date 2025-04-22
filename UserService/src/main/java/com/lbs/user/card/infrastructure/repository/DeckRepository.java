package com.lbs.user.card.infrastructure.repository;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.response.DeckResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface DeckRepository {
    Deck findById(Long id);
    Deck save(Deck deck);
    Deck update(Deck deck);
    Long delete(Long id);

    List<Deck> findBy();

    Card saveCard(Card card);
    Slice<DeckResponseDto> findByAll(Pageable pageable);

    Card deleteCard(Card card);

    Long deleteCard(Long id, Long cardId);
}

