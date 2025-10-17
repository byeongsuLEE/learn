package com.lbs.user.friend.service;

import com.lbs.user.friend.domain.Friend;
import com.lbs.user.friend.domain.FriendRequest;
import com.lbs.user.friend.dto.request.FriendRequestDto;
import com.lbs.user.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 * 풀이방법
 **/

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    @Override
    public List<Friend> getFriends(Long userId) {
        return friendRepository.getFriends(userId);
    }

    @Override
    public FriendRequest sendFriendRequest(FriendRequestDto friendRequestDto) {
        FriendRequest friendRequest = friendRequestDto.mapTodomain();

        FriendRequest createdFriendRequest = friendRepository.sendFriendRequest(friendRequest);
        return createdFriendRequest;
    }

    @Override
    public Friend acceptFriendRequest(Long friendRequestId) {
        Friend friend = friendRepository.acceptFriendRequest(friendRequestId);
        return friend;
    }

    @Override
    public List<FriendRequest> getFriendRequests(Long userId) {
        return friendRepository.getFriendRequest(userId);

    }

    @Override
    public void cancelFriendRequest(FriendRequestDto friendRequestDto) {

    }

    @Override
    public Friend deleteFriend(Long userId, Long friendId, String friendEmail) {
        return null;
    }
}
