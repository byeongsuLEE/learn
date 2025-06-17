package com.lbs.user.card.dto.request;

import com.lbs.user.card.domain.Card;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-23
 * 풀이방법
 **/

@Getter
@NoArgsConstructor
public class UpdateCardRequestDto {
    private Long cardId; // ← 수정 시 필수
    private String title;
    private String desc;

    public UpdateCardRequestDto(Long cardId, String title, String desc) {
        this.cardId = cardId;
        this.title = title;
        this.desc = desc;
    }
}

