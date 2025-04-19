package com.lbs.user.card.dto.request;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.card.domain.Card;
import com.lbs.user.card.dto.response.CardResponseDto;
import lombok.*;

import java.util.List;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-12
 * 풀이방법
 **/


@Getter
@NoArgsConstructor
@ToString
@Setter
public class DeckRequestDto {
    private Long id;
    private String title;
    private String desc;
    private String category;
    private String tag;
    private AuditInfo auditInfo;
    private List<Card> cards;
}
