package com.lbs.user.user.dto.response;

public record UserSettingResponseDto(
        boolean studyNotify,
        boolean friendActivityNotify,
        boolean achieveNotify,
        boolean emailNotify,
        String language,
        String theme,
        Integer studyGoal,
        boolean autoPlay,
        boolean profileVisible,
        boolean studyStatsVisible,
        boolean friendsCanMessage
) { }
