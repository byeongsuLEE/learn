package com.lbs.user.card.dto.request;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.card.domain.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-23
 * 풀이방법
 **/

@Getter
@NoArgsConstructor
@ToString
@Setter
public class UpdateDeckRequestDto {
    private Long id;
    private String title;
    private String desc;
    private String category;
    private String tag;
    private List<UpdateCardRequestDto> cards;
}
