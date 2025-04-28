package com.lbs.user.card.infrastructure.entity;

import com.lbs.user.card.domain.Card;
import com.lbs.user.user.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/

@Entity
@Table(name = "cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    // 나는 deck에서 해당 id가 없어지면 관련된 card들 모 두 지우고 싶음 =  cascade.remove만하면됨
    // 부모 삭제 시 card도 삭제 +
    @ManyToOne(fetch = FetchType.LAZY)  //
    @JoinColumn(name = "deck_id")
    private DeckEntity deck;

    // private를 두는 이유는 외부에서 entity를 못만들게 하기 위해서이다.
    @Builder
    private CardEntity(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }




    public void linkToDeck(DeckEntity deck) {
        this.deck = deck;
    }

    public static CardEntity createCardEntity (Card card){
        return CardEntity.builder()
                .id(card.getId())
                .title(card.getTitle())
                .description(card.getDesc())
                .build();
    }



//    // 정적 팩토리 : 도메인 제약 적용 할 수 있어 쓴다.
//    public static CardEntity create(String title, String description, DeckEntity deck) {
//        return CardEntity.builder()
//                .description(description)
//                .title(title)
//                .deckEntity(deckEntity)
//                .build();
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardEntity that = (CardEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public void unLinkToDeck() {
        this.deck = null;
    }

    public void updateCard(CardEntity card) {
        this.title = card.getTitle();
        this.description = card.getDescription();
    }



}
