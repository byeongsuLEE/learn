package com.lbs.user.user.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-12
 * 풀이방법
 **/

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserJoinResponseDto {
    private String email;
    private String password;
    private LocalDateTime joinDate;
}
