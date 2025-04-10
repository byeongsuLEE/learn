package com.lbs.user.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-10
 * deck Domain입니다.
 **/




@Getter
public class Deck {
     Long id;
     String title;
     String desc;
     String category;
     String tag;

    private Deck(Long id, String title, String desc, String category, String tag) {
         this.id = id;
         this.title = title;
         this.desc = desc;
         this.category = category;
         this.tag = tag;
     }

    private Deck(String title, String desc, String category, String tag) {
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.tag = tag;
    }

    //



    //update
    // dto -> domain -> entity
    // domain domain 객체를 바꿈 ->
    // domain -> entity
    // rrrrrrrrrrrr
    // domain long id => entity long id domain 객체를 넘겨주면 되지.
    //  request dto (id) -> entity id -> entitiy -> respons dto

     public static Deck createDeck(String title, String desc, String category, String tag) {
         return new Deck(title, desc, category, tag);
     }

     public static Deck infoDeck(Long id, String title, String desc, String category, String tag) {
         return new Deck(id, title, desc, category, tag);
     }

}
