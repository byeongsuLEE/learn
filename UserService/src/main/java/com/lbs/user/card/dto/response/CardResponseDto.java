package com.lbs.user.card.dto.response;

import com.lbs.user.card.domain.AuditInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/


@Data
@Builder
public class CardResponseDto {
    private Long id;
    private Long deckId;
    private String title;
    private String desc;
    private AuditInfo auditInfo;
}
