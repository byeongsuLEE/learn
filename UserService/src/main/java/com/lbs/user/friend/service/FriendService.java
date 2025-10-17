package com.lbs.user.friend.service;


import com.lbs.user.friend.domain.Friend;
import com.lbs.user.friend.domain.FriendRequest;
import com.lbs.user.friend.dto.request.FriendRequestDto;

import java.util.List;


public interface FriendService {
    List<Friend> getFriends(Long userId);
    FriendRequest sendFriendRequest(FriendRequestDto friendRequestDto);
    void cancelFriendRequest(FriendRequestDto friendRequestDto);
    Friend deleteFriend(Long userId, Long friendId, String friendEmail);
    Friend acceptFriendRequest(Long friendRequestId);
    List<FriendRequest> getFriendRequests(Long userId);

}
