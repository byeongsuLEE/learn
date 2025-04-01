package com.lbs.user.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 작성자  : lb
 * 날짜    : 2025-04-01
 * 풀이방법
 **/

@Builder
@Data
public class UserInfoResponseDto {
    private String email;
    private LocalDateTime joinDate;
}
