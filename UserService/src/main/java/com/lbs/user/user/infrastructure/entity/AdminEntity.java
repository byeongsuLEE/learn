package com.lbs.user.user.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-05
 * 풀이방법
 **/


@Entity
@Getter
@Setter
@DiscriminatorValue("Admin")
public class AdminEntity extends UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String str;

}
