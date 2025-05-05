package com.lbs.user.user.service;

import com.lbs.user.common.exception.UserNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.user.mapper.UserMapper;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import com.lbs.user.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-21
 * 풀이방법
 **/

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // 나중에 db가 두개이상이라면 @Qulifier("jpaRepository") 붙이기
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User joinUser(User user) {
        userPasswordEncoding(user);
        UserEntity userEntity = userMapper.userToEntity(user);
        UserEntity saveUserEntity = userRepository.save(userEntity);
        user = userMapper.userEntityToUser(saveUserEntity);
        return user;
    }



    private User userPasswordEncoding(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.encodingPassword(encodedPassword);
        return user;
    }

    @Override
    public User readUser(Long id) {
        UserEntity userInfo = userRepository.findById(id)
             .orElseThrow(()-> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        User user = userMapper.userEntityToUser(userInfo);
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User deleteUser(String id) {
        return null;
    }

    @Override
    public User readUserByEmail(String email) {
        UserEntity userInfo = userRepository.findByEmail(email)
                .orElseGet(null);

        if(userInfo == null) { return null;}

        User user = userMapper.userEntityToUser(userInfo);
        return user;
    }
}
