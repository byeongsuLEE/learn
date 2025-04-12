package com.lbs.user.card.infrastructure.repository.jpa;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import com.lbs.user.card.mapper.DeckMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-11
 * 풀이방법
 **/

@Component
@RequiredArgsConstructor
public class JpaDeckRepositoryAdapter implements DeckRepository {

    private final DeckMapper deckMapper;
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
        return null;
    }

    @Override
    public Deck delete(Deck deck) {
        return null;
    }


}
