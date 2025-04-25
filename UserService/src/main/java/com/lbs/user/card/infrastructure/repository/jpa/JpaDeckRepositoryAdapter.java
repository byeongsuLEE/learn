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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Deck findById(Long id) {
        Deck deck = jpaDeckRepository.findById(id).map(deckMapper::entityToDomain)
                .orElseThrow(() -> new DeckNotFoundException(ErrorCode.DECK_NOT_FOUND));

        return  deck;
    }

    @Override
    public Deck save(Deck deck) {
        DeckEntity deckEntity = deckMapper.domainToEntity(deck);
        jpaDeckRepository.save(deckEntity);
        return deckMapper.entityToDomain(deckEntity);
    }

    @Override
    public Deck update(Deck deck) {
        DeckEntity deckEntity =  jpaDeckRepository.findById(deck.getId())
                .orElseThrow(() -> new DeckNotFoundException(ErrorCode.DECK_NOT_FOUND));

        // 방법 1. 모든 카드를 지우고 삽입한다.
//        deckEntity.getCards().clear();
        deckEntity.updateDeck(deck);

        updateDeckCards(deck, deckEntity);
//


        //방법 2. 변경된 부분만 넣어주자.
        //이유 : 방법 1은 수정버튼을 여러번 눌렀을 때 삭제와 삽입 연산이 n번씩 나가니까 힘들 것 같음


        Deck updatedDeck = deckMapper.entityToDomain(deckEntity);
        return updatedDeck;
    }

    private static void updateDeckCards(Deck deck, DeckEntity deckEntity) {
        Map<Long, CardEntity> beforeCardMaps = deckEntity.getCards().stream().collect(Collectors.toMap(CardEntity::getId, Function.identity()));
        List<CardEntity> updatedCardList = deck.getCards().stream().map(CardEntity::createCardEntity).toList();

        for (CardEntity card : updatedCardList) {
            //새로운 카드면 insert
            if (beforeCardMaps.get(card.getId()) == null) {
                deckEntity.addCard(card);
            }
            //수정된 카드면 update
            else {
                CardEntity cardEntity = beforeCardMaps.get(card.getId());
                beforeCardMaps.remove(card.getId());
                cardEntity.updateCard(card);
            }
        }

        for (CardEntity deletedCard : beforeCardMaps.values()) {
            deckEntity.deleteCard(deletedCard);
        }
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
