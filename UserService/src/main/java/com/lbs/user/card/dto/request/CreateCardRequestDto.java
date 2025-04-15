package com.lbs.user.card.dto.request;

import lombok.Data;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/

@Data
public class CreateCardRequestDto {
    Long deckId;
    String title;
    String desc;
}
