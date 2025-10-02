package com.lbs.user.user.infrastructure.repository;

import com.lbs.user.user.domain.repository.UserSettingRepository;
import com.lbs.user.user.infrastructure.entity.UserSettingsEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-02
 * 풀이방법
 **/

@Repository
@Qualifier("jpaUserSettingRepository")
public interface UserSettingJPARepository extends JpaRepository<UserSettingsEntity, Long> {

    Optional<UserSettingsEntity> findByUserId(Long userId);

}
