package com.lbs.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-12
 * 풀이방법
 **/


@Builder
@Getter
@Setter
public class UserJoinRequestDto {

    @NotNull(message ="이메일은 빈 값이 될 수 없습니다.")
    @Size(min = 2 , message = "두 글자 이상 작성해주세요")
    @Email
    private String email;

    @NotNull(message = "비밀번호를 입력 해주세요")
    @Size(min =4,message = "4글자 이상 비밀번호를 입력해주세요")
    private String password;

    private LocalDateTime joinDate;
}
