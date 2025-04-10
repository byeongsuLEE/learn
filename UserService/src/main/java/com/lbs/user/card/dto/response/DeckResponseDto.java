package com.lbs.user.card.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/


@Getter
@Setter
@ToString
@NoArgsConstructor
public class DeckResponseDto {
    private Long id;
    private String title;
    private String desc;
    private String category;
    private String tag;

    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
