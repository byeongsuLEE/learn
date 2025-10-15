package com.lbs.user.user.service;

import com.lbs.user.user.domain.UserSettings;
import com.lbs.user.user.dto.request.UserSettingRequestDto;
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

    @Override
    public UserSettings updateSettings(Long userId, UserSettingRequestDto request) {
        UserSettings userSettings = UserSettings.requestToDomain(userId,request) ;

        UserSettings userSetting = userSettingRepository.update(userSettings);
        return userSetting;
    }

    @Override
    public UserSettings getSettings(Long userId) {
        return userSettingRepository.findByUserId(userId);
    }

    @Override
    public UserSettings createUserSettings(Long userId, UserSettingRequestDto request){
        UserSettings userSettings = UserSettings.requestToDomain(userId,request) ;
      return userSettingRepository.create( userSettings);
    }
}
