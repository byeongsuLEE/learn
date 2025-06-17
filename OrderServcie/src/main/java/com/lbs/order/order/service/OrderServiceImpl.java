package com.lbs.order.order.service;

import com.lbs.order.order.feign.UserServiceClient;
import com.lbs.order.order.feign.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-24
 * 풀이방법
 **/


@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final UserServiceClient userServiceClient;

    @Override
    public UserResponse getUser(Long userId) {

        return userServiceClient.getUser(userId).getBody().getData();
    }
}
