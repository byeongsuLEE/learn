package com.lbs.user.user.infrastructure.repository;

import com.lbs.user.common.exception.UserNotFoundException;
import com.lbs.user.common.exception.UserSettingNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.user.domain.UserSettings;
import com.lbs.user.user.domain.repository.UserSettingRepository;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import com.lbs.user.user.infrastructure.entity.UserSettingsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final JPARepository UserjpaRepository;

    @Override
    public UserSettings findByUserId(Long userId) {
        UserSettingsEntity userSettingsEntity = getUserSettingsEntity(userId);
        return userSettingsEntity.mapToDomain(userId,userSettingsEntity);

    }


    @Override
    public UserSettings update(UserSettings userSettings) {

        UserSettingsEntity userSettingsEntity = getUserSettingsEntity(userSettings);
        userSettingsEntity.updateUserSettings(userSettings);
        return userSettingsEntity.mapToDomain(userSettings.userId(),userSettingsEntity);
    }

    @Override
    public UserSettings create(UserSettings userSettings) {
        // User Entity 조회 (새로 생성하는 경우에만 필요)
        UserEntity user = UserjpaRepository.findById(userSettings.userId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 새 Entity 생성
        UserSettingsEntity newEntity = UserSettingsEntity.create(userSettings, user);

        // 저장
        UserSettingsEntity savedEntity = userSettingJPARepository.save(newEntity);
        return savedEntity.mapToDomain(userSettings.userId(), savedEntity);
    }



    private UserSettingsEntity getUserSettingsEntity(UserSettings userSettings) {

        UserSettingsEntity  userSettingsEntity = getUserSettingsEntity(userSettings.userId());

        return userSettingsEntity;
    }

    /**
     *
     * @작성자   : lbs
     * @작성일   : 2025-10-02
     * @설명     : 유저 id를 이용한 유저 설정 정보 가져오기
     * @param
     * @return
     */
    private UserSettingsEntity getUserSettingsEntity(Long userId) {
        UserSettingsEntity userSettingsEntity = userSettingJPARepository.findByUserId(userId)
                .orElseThrow(() -> new UserSettingNotFoundException(ErrorCode.USER_SETTING_NOT_FOUND));
        return userSettingsEntity;
    }

}
