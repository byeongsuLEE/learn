package com.lbs.user.user.dto.request;

public record UserSettingRequestDto(

        Long id,

        // 알림 설정
        Boolean studyNotify,
        Boolean friendActivityNotify,
        Boolean achieveNotify,
        Boolean emailNotify,

        // 학습 설정
        String language,
        String theme,
        Integer studyGoal,
        Boolean autoPlay,

        // 개인정보 설정
        Boolean profileVisible,
        Boolean studyStatsVisible,
        Boolean friendsCanMessage


) {
}
