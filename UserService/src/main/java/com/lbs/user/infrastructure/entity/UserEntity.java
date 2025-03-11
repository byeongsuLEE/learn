package com.lbs.user.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 작성자  : 이병수
 * 날짜    : 2025-03-05
 * 풀이방법
 **/

@Table(name="users")
@Entity
public class UserEntity {
    @Id
    @GeneratedValue
    Long id ;


}
