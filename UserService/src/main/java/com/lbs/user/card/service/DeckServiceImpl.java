package com.lbs.user.card.service;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import com.lbs.user.common.exception.DeckNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Deck saveDeck(Deck deck) {
        return deckRepository.save(deck);
//        DeckEntity deckEntity = deckMapper.domainToEntity(card);
//        deckRepository.save(deckEntity);
//        DeckResponseDto deckResponseDto = deckMapper.entityToResponseDto(deckEntity);
//        return deckEntity.getId();
    }

    @Override
    public List<Deck> readAllDecks() {
        return deckRepository.findBy();
    }



    @Override
    public Card createCard(Card card) {
        return deckRepository.saveCard(card);
    }



    @Override
    public Deck readDeck(Long id) {
        Deck deck = deckRepository.findById(id);
        return deck;
    }
    @Override
    public Deck updateDeck(Deck deck) {

        return deckRepository.update(deck);
    }

    @Override
    public Long deleteDeck(Long id) {
        return deckRepository.delete(id);
    }

    @Override
    public Long deleteCard(Long id, Long cardId) {
        return deckRepository.deleteCard(id,cardId);
    }
}
