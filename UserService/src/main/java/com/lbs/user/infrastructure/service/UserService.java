package com.lbs.user.infrastructure.service;


import org.springframework.stereotype.Service;


import com.lbs.user.domain.User;
import org.springframework.stereotype.Service;


public interface UserService {
    User joinUser(User user);
    User readUser(String id);
    User updateUser(User user);
    User deleteUser(String id);
}

