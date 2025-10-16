package com.lbs.user.friend.repository;

import com.lbs.user.friend.domain.Friend;
import com.lbs.user.friend.domain.FriendRequest;
import com.lbs.user.friend.dto.request.FriendRequestDto;

import java.util.List;


public interface FriendRepository {
    // 친구 요청 보내기(생성)
    List<Friend> getFriends(Long userId);
    Friend deleteFriend(Long userId, Long friendId, String friendEmail);

    FriendRequest sendFriendRequest(FriendRequest friendRequest);
    Friend acceptFriendRequest(Long friendRequestDto);
}
