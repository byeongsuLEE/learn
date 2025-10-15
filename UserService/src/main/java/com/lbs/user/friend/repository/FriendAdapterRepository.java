package com.lbs.user.friend.repository;

import com.lbs.user.common.exception.UserNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.friend.domain.Friend;
import com.lbs.user.friend.domain.FriendRequest;
import com.lbs.user.friend.dto.request.FriendRequestDto;
import com.lbs.user.friend.infrastructure.jpa.FriendJPARepository;
import com.lbs.user.friend.infrastructure.jpa.FriendRequestJPARepository;
import com.lbs.user.user.infrastructure.entity.FriendEntity;
import com.lbs.user.user.infrastructure.entity.FriendRequestEntity;
import com.lbs.user.user.infrastructure.entity.FriendRequestStatus;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import com.lbs.user.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 * 풀이방법
 **/

@Repository
@RequiredArgsConstructor
public class FriendAdapterRepository implements FriendRepository {

    private final FriendJPARepository friendJPARepository;
    private final FriendRequestJPARepository friendRequestJPARepository ;
    private final UserRepository userRepository;

    @Override
    public List<Friend> getFriends(Long userId) {
        List<FriendEntity> friendEntityList = friendJPARepository.findByOwner_Id(userId);

        List<Friend> friendList = friendEntityList.stream()
                .map((f) -> FriendEntity.mapToDomain(f))
                .toList();

        return friendList;
    }

    @Override
    public Friend deleteFriend(Long userId, Long friendId, String friendEmail) {
        return null;
    }

    @Override
    public FriendRequest sendFriendRequest(FriendRequest friendRequest) {




        //user repository 에서 sender , receiver 가져오기

        UserEntity sender = userRepository.findById(friendRequest.getSenderId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        UserEntity receiver = userRepository.findById(friendRequest.getReceiverId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        Optional<FriendRequestEntity> bySenderAndReceiverAndStatus = friendRequestJPARepository.findBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING);


        return friendRequestJPARepository.findBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING)
                 // 전에 보냈던 요청이 있다면 db에는 다시 저장하지 안하기
                 .map(FriendRequestEntity::entityToDomain)
                 // 이전의 보낸 요청이 없다면 데이터 베이스에 저장하기
                .orElseGet(()->{
                    FriendRequestEntity friendRequestEntity = FriendRequestEntity.createFriendRequestEntity(sender, receiver, friendRequest.getStatus());

                    FriendRequestEntity savedEntity = friendRequestJPARepository.save(friendRequestEntity);

                    return savedEntity.entityToDomain();
                });

    }

    @Override
    public void cancelFriendRequest(FriendRequestDto friendRequestDto) {

    }

    @Override
    public void acceptFriendRequest(FriendRequestDto friendRequestDto) {

    }
}
