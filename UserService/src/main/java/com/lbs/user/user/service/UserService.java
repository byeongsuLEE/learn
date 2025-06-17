package com.lbs.user.user.service;


import com.lbs.user.user.domain.User;


public interface UserService {
    User joinUser(User user);
    User readUser(Long id);
    User updateUser(User user);
    User deleteUser(String id);

    User readUserByEmail(String email);
}

