package com.lbs.user.friend.infrastructure.jpa;

import com.lbs.user.user.infrastructure.entity.FriendEntity;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 *
 **/

public interface FriendJPARepository extends JpaRepository<FriendEntity,Long> {


    // _ 언더바 : user1 내부로 진입한다는 뜻
    List<FriendEntity> findByOwner_Id(Long id);
}
