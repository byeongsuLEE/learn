package com.lbs.user.card.service;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-10
 * 풀이방법
 **/

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    @Override
    public Deck createDeck(Deck deck) {
        return deckRepository.save(deck);
//        DeckEntity deckEntity = deckMapper.domainToEntity(card);
//        deckRepository.save(deckEntity);
//        DeckResponseDto deckResponseDto = deckMapper.entityToResponseDto(deckEntity);
//        return deckEntity.getId();
    }

    @Override
    public Deck readDeck(Long id) {
        return null;
    }

    @Override
    public Deck updateDeck(Deck card) {
        return null;
    }

    @Override
    public Deck deleteDeck(Long id) {
        return null;
    }
}
