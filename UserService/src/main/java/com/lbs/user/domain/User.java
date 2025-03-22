package com.lbs.user.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 **/

@Builder
@Getter
@NoArgsConstructor
@ToString
public class User {
    private String email;
    private String password;
    private LocalDateTime joinDate;
    public User(String email, String password, LocalDateTime joinDate) {
        this.email = email;
        this.password = password;
        this.joinDate = (joinDate == null) ? LocalDateTime.now() : joinDate;
    }

    public static class UserBuilder{
        public User build(){
            if(this.joinDate ==null)  this.joinDate = LocalDateTime.now();
            return  new User(email, password, joinDate);
        }
    }
}
