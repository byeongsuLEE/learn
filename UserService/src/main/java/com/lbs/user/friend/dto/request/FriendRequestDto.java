package com.lbs.user.friend.dto.request;

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
}
