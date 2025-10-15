package com.lbs.user.friend.domain;

import com.lbs.user.friend.dto.request.FriendRequestDto;
import com.lbs.user.user.infrastructure.entity.FriendRequestStatus;
import lombok.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-13
 * 풀이방법
 **/


// 궁금증 : @builder+ @NoArgsConstructor는 적절한 생성자가 필요하다고 안내문이 나온다.
    // 어떤 것이 적절한건가?
// 이유
// - 빌더는 내부적으로 모든 필드를 가진 생성자를 호출하는데 noargsConstructor를 사용하게되면 모든 필드를 가진 생성자가 만들어지지 않는다.
// - 따라서 적절한 생성자가 필요하다는 에러가 나온다.
// 결론
// 1. noargsConstrucotr를 제거 or allargsConstructor를 추가

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class FriendRequest {
    Long senderId;
    Long receiverId;
    FriendRequestStatus status;

    /**
     * 친구 요청 도메인 객체 생성
     * @작성자   : lbs
     * @작성일   : 2025-10-13
     * @설명     : 무분별한 도메인 객체 생성을 막고자 클래스 내부에서만 builder 패턴을 이용한 생성자 메서드
     * @param request
     * @return
     */
    public static FriendRequest  createFirstFriendRequest(FriendRequestDto request) {
        return  FriendRequest.builder()
                .receiverId(request.getReceiverId())
                .senderId(request.getSenderId())
                .status(FriendRequestStatus.PENDING)
                .build();
    }

    public static FriendRequest  createFriendRequest(Long senderId, Long receiverId, FriendRequestStatus status) {
        return  FriendRequest.builder()
                .receiverId(senderId)
                .senderId(receiverId)
                .status(status)
                .build();
    }



    public FriendRequest changeStatus(FriendRequestStatus status) {
        this.status = status;
        return this;
    }




}
