package com.lbs.user.friend.infrastructure.jpa;

import com.lbs.user.user.infrastructure.entity.FriendEntity;
import com.lbs.user.user.infrastructure.entity.FriendRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 * 풀이234Z 방법
 **/


public interface FriendRequestJPARepository extends JpaRepository<FriendRequestEntity, Long> {
}

