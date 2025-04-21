package com.lbs.user.card.infrastructure.repository.jpa;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.infrastructure.entity.CardEntity;
import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import com.lbs.user.card.mapper.CardMapper;
import com.lbs.user.card.mapper.DeckMapper;
import com.lbs.user.common.exception.DeckNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Delete;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-11
 * 풀이방법
 **/

@Component
@RequiredArgsConstructor
@Transactional
public class JpaDeckRepositoryAdapter implements DeckRepository {


    private final DeckMapper deckMapper;
    private final CardMapper cardMapper;
    private final JpaDeckRepository jpaDeckRepository;

    @Override
    public List<Deck> findBy() {
        List<DeckEntity> deckEntityList = jpaDeckRepository.findAll();
        Deck deck = deckMapper.entityToDomain(deckEntityList.get(0));
        List<Deck> decks = deckEntityList.stream().map(deckMapper::entityToDomain).toList();
        return decks;
    }


    @Override
    public Optional<Deck> findById(Long id) {
        Optional<Deck> deck1 = jpaDeckRepository.findById(id).map(deckMapper::entityToDomain);

        return  deck1;
    }

    @Override
    public Deck save(Deck deck) {
        DeckEntity deckEntity = deckMapper.domainToEntity(deck);
        jpaDeckRepository.save(deckEntity);
        return deckMapper.entityToDomain(deckEntity);
    }

    @Override
    public Deck update(Deck deck) {
        DeckEntity deckEntity =  jpaDeckRepository.findById(deck.getId()).orElse(null);
        if(deckEntity == null) return null;
        deckEntity.updateDeck(deck);
        Deck updatedDeck = deckMapper.entityToDomain(deckEntity);
        return updatedDeck;
    }

    @Override
    public Long delete(Long id ) {
        jpaDeckRepository.deleteById(id);
        return id;

    }

    @Override
    public Card saveCard(Card card) {
//        System.out.println(card.getDeckId());
        DeckEntity deckEntity = jpaDeckRepository.findById(card.getDeckId())
                .orElseThrow(() -> new DeckNotFoundException(ErrorCode.DECK_NOT_FOUND));

        CardEntity cardEntity = CardEntity.createCardEntity(card);
        deckEntity.addCard(cardEntity);

        //card entity에 값을 넣기 위해서
        jpaDeckRepository.save(deckEntity);
        card = cardMapper.entityToDomain(cardEntity);
        return card;
    }

    @Override
    public Slice<DeckResponseDto> findByAll(Pageable pageable) {
        Slice<DeckEntity> all = jpaDeckRepository.findAll(pageable);
        Slice<DeckResponseDto> map = all.map(deckMapper::entityToResponseDto);
        return map;
    }

    @Override
    public Card deleteCard(Card card) {

        DeckEntity deckEntity = jpaDeckRepository.findById(card.getDeckId())
                .orElseThrow(() -> new DeckNotFoundException(ErrorCode.DECK_NOT_FOUND));

        CardEntity cardEntity = cardMapper.domainToEntity(card);
        deckEntity.deleteCard(cardEntity);

        return card;
    }

    @Override
    public Long deleteCard(Long deckId, Long cardId) {
        DeckEntity deckEntity = jpaDeckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(ErrorCode.DECK_NOT_FOUND));

        CardEntity cardEntity = cardMapper.domainToEntity(Card.createCard(cardId,null,null));
        deckEntity.deleteCard(cardEntity);

        return cardId;
    }


}
