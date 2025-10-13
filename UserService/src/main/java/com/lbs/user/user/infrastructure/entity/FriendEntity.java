package com.lbs.user.user.infrastructure.entity;

import com.lbs.user.friend.domain.Friend;
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
    @JoinColumn(name = "friend")
    UserEntity friend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    UserEntity owner;

    public static Friend mapToDomain(FriendEntity friendEntity) {
        return Friend.builder()
                .id(friendEntity.getId())
                .friendUser(UserEntity.mapToDomain(friendEntity.getFriend()))
                .build();
    }
}
