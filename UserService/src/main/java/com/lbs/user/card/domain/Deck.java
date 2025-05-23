package com.lbs.user.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
     AuditInfo auditInfo;
     List<Card> cards = new ArrayList<>();
     int cardCount ;
    private Deck(Long id, String title, String desc, String category, String tag, List<Card> cards ) {
         this.id = id;
         this.title = title;
         this.desc = desc;
         this.category = category;
         this.tag = tag;
         this.cards = cards;
     }


    private Deck(Long id, String title, String desc, String category, String tag, AuditInfo auditInfo,List<Card> cards) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.tag = tag;
        this.auditInfo = auditInfo;
        this.cards = cards;
    }

    private Deck(String title, String desc, String category, String tag) {
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.tag = tag;
    }

    public Deck(Long id, String title, String desc, String category, String tag, AuditInfo auditInfo, List<Card> cards, int cardCount) {

        this.id = id;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.tag = tag;
        this.auditInfo = auditInfo;
        this.cards = cards;

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

    public static Deck createDeck(Long id , String title, String desc, String category, String tag, AuditInfo auditInfo,List<Card> cards) {
        return new Deck(id, title, desc, category, tag,auditInfo,cards);
    }

     public static Deck infoDeck(Long id, String title, String desc, String category, String tag, AuditInfo auditInfo,List<Card> cards) {
         return new Deck(id, title, desc, category, tag,auditInfo,cards);
     }

    public static Deck createDeck(Long id, String title, String desc, String category, String tag, List<Card> list) {
        return  new Deck(id, title, desc, category, tag,list);
    }

    public static Deck createDeck(Long id, String title, String desc, String category, String tag, AuditInfo auditInfo, List<Card> cards, int cardCount) {
        return new Deck(id, title, desc, category, tag,auditInfo,cards,cardCount);

    }

    public void addAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
     }

}
