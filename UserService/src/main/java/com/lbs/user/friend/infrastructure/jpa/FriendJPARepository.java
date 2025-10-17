package com.lbs.user.friend.infrastructure.jpa;

import com.lbs.user.user.infrastructure.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 *
 **/

public interface FriendJPARepository extends JpaRepository<FriendEntity,Long> {


    // _ 언더바 : user1 내부로 진입한다는 뜻
    List<FriendEntity> findByRequester_id(Long id);
}
