package com.lbs.user.card.infrastructure.entity;

import com.lbs.user.user.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
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


    @Builder
    private DeckEntity(String title, String description, String tag, String category) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.category = category;
    }

    public static DeckEntity createDeck(String title, String description, String tag, String category){
        return DeckEntity.builder()
                .title(title)
                .description(description)
                .tag(tag)
                .category(category)
                .build();
    }

}
