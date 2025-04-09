package com.lbs.user.card.infrastructure.entity;

import com.lbs.user.user.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/

@Entity
@Table(name = "cards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id")
    private Deck deck;

    // private를 두는 이유는 외부에서 entity를 못만들게 하기 위해서이다.
    @Builder
    private CardEntity(String title, String description, Deck deck) {
        this.title = title;
        this.description = description;
        this.deck = deck;
    }


    // 정적 팩토리 : 도메인 제약 적용 할 수 있어 쓴다.
    public static CardEntity create(String title, String description, Deck deck) {
        return CardEntity.builder()
                .description(description)
                .title(title)
                .deck(deck)
                .build();
    }

}
