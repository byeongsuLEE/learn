package com.lbs.user.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 작성자  : lb
 * 날짜    : 2025-04-01
 * 풀이방법
 **/


@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private LocalDateTime joinDate;
}
