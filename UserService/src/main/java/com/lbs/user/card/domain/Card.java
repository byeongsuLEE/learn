package com.lbs.user.card.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-13
 * 풀이방법
 **/



@Getter
@NoArgsConstructor
public class Card {
    private Long id;
    private Long deckId;
    private String title;
    private String desc;
    private AuditInfo auditInfo;

    private Card(Long id , Long deckId, String title, String description, AuditInfo aduitInfo) {
        this.id = id;
        this.deckId = deckId;
        this.title = title;
        this.desc = description;
        this.auditInfo = aduitInfo;
    }

    private Card(Long deckId , String title, String description) {
        this.deckId = deckId;
        this.title = title;
        this.desc = description;
    }

    public Card(Long deckId, Long cardId, String title, String desc) {
        this.deckId = deckId;
        this.id = cardId;
        this.title = title;
        this.desc = desc;
    }

    public static Card createCard(Long id, Long deckId , String title, String description, AuditInfo auditInfo) {
        return new Card(id,deckId, title,description,auditInfo);
    }

    public static Card createCard(Long deckId, String title, String description) {
        return new Card(deckId, title,description);
    }

    public static Card createCard(Long deckId, Long cardId, String title, String desc) {
        return new Card(deckId,cardId,title,desc);
    }

    @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Card card = (Card) o;
            return Objects.equals(getId(),card.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
}
