package com.lbs.user.card.dto.request;

import lombok.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-10
 * 풀이방법
 **/

@Getter
@Setter
@ToString
@NoArgsConstructor

public class CreateDeckRequestDto {
    private String title;
    private String desc;
    private String category;
    private String tag;
}
