package com.lbs.user.user.domain.repository;

import com.lbs.user.user.domain.UserSettings;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingRepository {
    UserSettings update(UserSettings userSettings);
    UserSettings findByUserId(Long userId);

    UserSettings create(UserSettings userSettings);
}
