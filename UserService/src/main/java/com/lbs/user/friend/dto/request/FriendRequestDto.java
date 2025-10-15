package com.lbs.user.friend.dto.request;

import com.lbs.user.friend.domain.FriendRequest;
import com.lbs.user.user.infrastructure.entity.FriendRequestStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-12
 * 풀이방법
 **/


@Data
public class FriendRequestDto {
    Long senderId;
    Long receiverId;
    String receiverEmail;
    FriendRequestStatus status;

    public  FriendRequest mapTodomain() {
        return FriendRequest.createFirstFriendRequest(this);
    }
}
