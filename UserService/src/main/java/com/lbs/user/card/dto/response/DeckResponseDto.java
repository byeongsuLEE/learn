package com.lbs.user.card.dto.response;

import com.lbs.user.card.domain.AuditInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

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
    private AuditInfo auditInfo;
    private int cardCount;
    private List<CardResponseDto> cards;
}
