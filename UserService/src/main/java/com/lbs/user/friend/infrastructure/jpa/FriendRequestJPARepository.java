package com.lbs.user.friend.infrastructure.jpa;

import com.lbs.user.user.infrastructure.entity.FriendEntity;
import com.lbs.user.user.infrastructure.entity.FriendRequestEntity;
import com.lbs.user.user.infrastructure.entity.FriendRequestStatus;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import com.netflix.appinfo.ApplicationInfoManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 * 풀이234Z 방법
 **/


public interface FriendRequestJPARepository extends JpaRepository<FriendRequestEntity, Long> {

    Optional<FriendRequestEntity> findBySenderAndReceiverAndStatus(UserEntity sender, UserEntity receiver, FriendRequestStatus status);

    Optional<FriendRequestEntity> findBySenderAndReceiver(UserEntity sender, UserEntity receiver);
}

