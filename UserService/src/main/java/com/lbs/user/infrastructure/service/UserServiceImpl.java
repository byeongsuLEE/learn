package com.lbs.user.infrastructure.service;

import com.lbs.user.common.mapper.UserMapper;
import com.lbs.user.domain.User;
import com.lbs.user.infrastructure.entity.UserEntity;
import com.lbs.user.infrastructure.repository.JPARepository;
import com.lbs.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-21
 * 풀이방법
 *
 **/

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    // 나중에 db가 두개이상이라면 @Qulifier("jpaRepository") 붙이기
    private final UserRepository userRepository;
    private final UserMapper userMapper ;

    @Override
    public User joinUser(User user) {
        UserEntity userEntity = userMapper.userToEntity(user);
        UserEntity saveUserEntity = userRepository.save(userEntity);
        user = userMapper.userEntityToUser(saveUserEntity);
        return user;
    }

    @Override
    public User readUser(String id) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User deleteUser(String id) {
        return null;
    }
}
