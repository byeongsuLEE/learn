package com.lbs.user.user.domain.repository;

import com.lbs.user.user.domain.UserSettings;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserSettingRepository {
    UserSettings save(UserSettings userSettings);
    UserSettings findByUserId(Long userId);
}
