package com.lbs.user.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 **/

@Data
public class User {
    private String email;
    private String password;
    private LocalDateTime joinDate;

}
