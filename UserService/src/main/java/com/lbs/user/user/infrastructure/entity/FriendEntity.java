package com.lbs.user.user.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-09
 * 풀이방법
 **/

@Entity
@Table(name = "friends")
@NoArgsConstructor
@Getter
public class FriendEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1")
    UserEntity user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2")
    UserEntity user2;

}
