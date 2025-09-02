package com.lbs.user.video.dto.response;

import com.lbs.user.card.domain.AuditInfo;

public record VideoResponseDto(
        Long id,
        String title,
        String description,
        String tag,
        String url,
        Long userId,
        AuditInfo auditInfo
) {}
