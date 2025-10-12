package com.lbs.user.user.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-11
 * 풀이방법
 **/


@Entity
@NoArgsConstructor //프록시를
@Getter
@Table(name = "friend_requests")
public class FriendRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "sender_id")
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_id")
    private UserEntity receiver;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false , columnDefinition = "varchar(20) default 'PENDING'")
    private FriendRequestStatus status;


}
