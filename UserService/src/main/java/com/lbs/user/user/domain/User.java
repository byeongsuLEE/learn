package com.lbs.user.user.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 **/

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String email;
    private String password;
    private LocalDateTime joinDate;


    public static class UserBuilder{
        public User build(){
            if(this.joinDate ==null)  this.joinDate = LocalDateTime.now();
            return  new User(email, password, joinDate);
        }
    }
    public void encodingPassword(String password) {
        this.password = password;
    }
}
