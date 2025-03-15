package com.lbs.user.infrastructure.entity;

import jakarta.persistence.*;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 * 풀이방법
 **/

@Table(name="users")
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id ;
    private String email;
    private String password;

}
