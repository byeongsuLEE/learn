package com.lbs.user.user.infrastructure.entity;

import com.lbs.user.friend.domain.FriendRequest;
import jakarta.persistence.*;
import lombok.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-11
 * 풀이방법
 **/


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) //프록시를
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


    // entity를 외부부터 생성 제한을 위한 접근 제어 설정
    @Builder(access = AccessLevel.PRIVATE)
    private FriendRequestEntity( Long id, UserEntity sender, UserEntity receiver, FriendRequestStatus status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    // 엔티티의 생성 시 행위를 표현하기 위한 메서드
    public static FriendRequestEntity createFriendRequestEntity(UserEntity sender, UserEntity receiver, FriendRequestStatus status) {
        return FriendRequestEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .status(status)
                .build();
    }

    public static FriendRequest entityToDomain(FriendRequestEntity e) {
        String email = e.getSender().getEmail();

        return FriendRequest.createFriendRequest(e.sender.getId(),e.receiver.getId(),e.status);
    }

    public static FriendRequest entityToDomain(FriendRequestEntity e, Long myId) {

        String friendEmail = myId == e.sender.getId() ? e.getReceiver().getEmail() : e.getSender().getEmail();
        String friendName = myId == e.sender.getId() ? e.getReceiver().getName() : e.getSender().getName();
        Long friendId  = myId == e.sender.getId() ?  e.getReceiver().getId() :e.getSender().getId();

        return FriendRequest.createFriendRequest(friendId,myId,e.status,friendEmail,friendName);
    }

//    public  FriendRequest entityToDomain(){
//
//        return FriendRequest.createFriendRequest(this.sender.getId(),this.receiver.getId(),this.status);
//    }
//
//    public FriendRequestEntity mapToDomain(FriendRequest friendRequest, UserEntity sender, UserEntity receiver){
//        return FriendRequestEntity.builder()
//                .receiver(receiver)
//                .sender(sender)
//                .status(friendRequest.getStatus())
//                .build();
//    }

    public FriendRequestEntity (FriendRequest friendRequest) {



    }

    public void acceptFriendRequest() {
        this.status = FriendRequestStatus.ACCEPTED;
    }
}
