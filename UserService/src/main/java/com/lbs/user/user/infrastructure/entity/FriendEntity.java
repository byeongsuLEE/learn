package com.lbs.user.user.infrastructure.entity;

import com.lbs.user.friend.domain.Friend;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-09
 * 풀이방법
 **/

@Builder
@Entity
@Table(name = "friends")
@NoArgsConstructor
@Getter
public class FriendEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    UserEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accpeter_id")
    UserEntity accepter;


    public FriendEntity(Long id, UserEntity requester, UserEntity accepter) {
        this.id = id;
        this.requester = requester;
        this.accepter = accepter;
    }

    public static Friend mapToDomain(FriendEntity friendEntity) {
        return Friend.builder()
                .id(friendEntity.getId())
                .friendUser(UserEntity.mapToDomain(friendEntity.getRequester()))
                .build();
    }
//
//    public static FriendEntity domainToEntity(FriendRequest friendRequest) {
//
//
//    }
}
