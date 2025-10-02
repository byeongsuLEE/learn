package com.lbs.user.user.domain;

import com.lbs.user.user.dto.response.UserSettingResponseDto;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-02
 * 풀이방법
 **/


public record UserSettings (
    Long id,
    Long userId,
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
){
    public UserSettingResponseDto userSettingToDto() {
        return new UserSettingResponseDto(
                this.studyNotify,
                this.friendActivityNotify,
                this.achieveNotify,
                this.emailNotify,
                this.language,
                this.theme,
                this.studyGoal,
                this.autoPlay,
                this.profileVisible,
                this.studyStatsVisible,
                this.friendsCanMessage
        );
    }
}
