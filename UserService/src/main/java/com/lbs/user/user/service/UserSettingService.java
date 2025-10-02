package com.lbs.user.user.service;

import com.lbs.user.user.domain.UserSettings;
import com.lbs.user.user.dto.request.UserSettingRequestDto;

public interface UserSettingService {
    UserSettings updateSettings(Long id, UserSettingRequestDto userSettingRequestDto);
    UserSettings getSettings(Long userId);


}
