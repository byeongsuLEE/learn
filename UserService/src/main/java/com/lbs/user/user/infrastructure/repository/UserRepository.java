package com.lbs.user.user.infrastructure.repository;

import com.lbs.user.user.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<UserEntity> findById(Long id);
    UserEntity save(UserEntity userEntity);

}
