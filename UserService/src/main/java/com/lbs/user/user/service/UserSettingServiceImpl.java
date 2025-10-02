package com.lbs.user.user.service;

import com.lbs.user.common.exception.UserNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.user.domain.UserSettings;
import com.lbs.user.user.dto.request.UserSettingRequestDto;
import com.lbs.user.user.infrastructure.entity.UserSettingsEntity;
import com.lbs.user.user.infrastructure.repository.UserRepository;
import com.lbs.user.user.domain.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-02
 * 풀이방법
 **/


@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {

    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;

    @Override
    public UserSettings updateSettings(Long userId, UserSettingRequestDto request) {
        UserSettings userSettings = new UserSettings(request.id(), userId,request.studyNotify(),
                request.friendActivityNotify(),
                request.achieveNotify(),
                request.emailNotify(),
                request.language(),
                request.theme(),
                request.studyGoal(),
                request.autoPlay(),
                request.profileVisible(),
                request.studyStatsVisible(),
                request.friendsCanMessage());

        UserSettings userSetting = userSettingRepository.save(userSettings);
        return userSetting;
    }

    @Override
    public UserSettings getSettings(Long userId) {
        return userSettingRepository.findByUserId(userId);
    }
}
