package com.lbs.user.card.infrastructure.repository;

import com.lbs.user.user.infrastructure.entity.UserEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-21
 * 풀이방법
 **/

@Repository
@Qualifier("jpaUserRepository")
public interface JPARepository extends JpaRepository<UserEntity, Long>, UserRepository {
 Optional<UserEntity> findById(Long id);
 UserEntity save(UserEntity userEntity);
}
