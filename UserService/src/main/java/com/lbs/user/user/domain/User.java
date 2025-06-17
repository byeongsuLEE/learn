package com.lbs.user.user.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 **/

@Builder
@Getter
@NoArgsConstructor
@ToString
public class User {
    private Long id;
    private String email;
    private String password;
    private LocalDateTime joinDate;
    private Address address;

    //ouath2
    private String name;
    private String imageUrl;
    private String provider;
    private String providerId;
    private List<String> roles;

    @Builder
    public User(Long id, String email, String password, LocalDateTime joinDate,
                Address address, String name, String imageUrl,
                String provider, String providerId, List<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.joinDate = joinDate != null ? joinDate : LocalDateTime.now();
        this.address = address;
        this.name = name;
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public void encodingPassword(String password) {
        this.password = password;
    }
}
