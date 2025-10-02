package com.lbs.user.user.infrastructure.repository;

import com.lbs.user.common.exception.UserSettingNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.user.domain.UserSettings;
import com.lbs.user.user.domain.repository.UserSettingRepository;
import com.lbs.user.user.infrastructure.entity.UserSettingsEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-02
 * 풀이방법
 **/
@Component
@Transactional
@RequiredArgsConstructor
public class UserSettingAdapter implements UserSettingRepository {

    private final UserSettingJPARepository userSettingJPARepository;

    @Override
    public UserSettings findByUserId(Long userId) {
        UserSettingsEntity userSettingsEntity = getUserSettingsEntity(userId);
        return userSettingsEntity.mapToDomain(userId,userSettingsEntity);

    }


    @Override
    public UserSettings save(UserSettings userSettings) {
        UserSettingsEntity userSettingsEntity = getUserSettingsEntity(userSettings.userId());
        userSettingsEntity.updateUserSettings(userSettings);
        return userSettingsEntity.mapToDomain(userSettings.userId(),userSettingsEntity);
    }

    /**
     *
     * @작성자   : lbs
     * @작성일   : 2025-10-02
     * @설명     : 유저 id를 이용한 유저 설정 정보 가져오기
     * @param userSettings
     * @return
     */
    private UserSettingsEntity getUserSettingsEntity(Long userSettings) {
        UserSettingsEntity userSettingsEntity = userSettingJPARepository.findByUserId(userSettings)
                .orElseThrow(() -> new UserSettingNotFoundException(ErrorCode.USER_SETTING_NOT_FOUND));
        return userSettingsEntity;
    }

}
