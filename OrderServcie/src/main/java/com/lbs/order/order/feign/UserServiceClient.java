package com.lbs.order.order.feign;

import com.lbs.order.order.feign.response.UserResponse;
import com.lbs.order.order.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/user-service/info/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable("id") Long id);

}
