package com.lbs.order.order.service;

import com.lbs.order.order.feign.response.UserResponse;

public interface OrderService {
    UserResponse getUser(Long userId);
}
