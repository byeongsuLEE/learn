package com.lbs.order.order.feign.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 작성자  : lbs
 * 날짜    : 2025-05-24
 * 풀이방법
 **/


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserResponse {
    private String email;
    private LocalDateTime joinDate;
}
