package com.lbs.user.card.infrastructure.entity;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.mapper.CardMapper;
import com.lbs.user.user.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법e
 **/

@Entity
@Getter
@Table(name = "decks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeckEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String tag;
    private String category;

    private int cardCount;
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CardEntity> cards = new ArrayList<>();


    @Builder
    private DeckEntity(String title, String description, String tag, String category) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.category = category;
    }

    public static DeckEntity createDeck(String title, String description, String tag, String category) {
        return DeckEntity.builder()
                .title(title)
                .description(description)
                .tag(tag)
                .category(category)
                .build();
    }

    public void updateDeck(Deck deck) {
        this.title = deck.getTitle();
        this.category = deck.getCategory();
        this.tag = deck.getTag();
        this.description = deck.getDesc();

    }



    public void addCard(CardEntity card) {
        this.cards.add(card);
        this.cardCount++;
        card.linkToDeck(this);
    }

    public void deleteCard(CardEntity card) {
        this.cards.remove(card);
        this.cardCount--;
        card.unLinkToDeck();
    }

    public void updateCardCount(int count){
        this.cardCount = count;
    }
}
