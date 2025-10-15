package com.lbs.user.friend.dto.response;

import com.lbs.user.friend.dto.FriendStatus;
import com.lbs.user.user.infrastructure.entity.FriendRequestStatus;
import lombok.*;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-12
 * 풀이방법
 **/

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendResponseDto {
    // 친구 이름
    String name;
    // 친구 이메일 or 아이디
    String email;
    FriendStatus status;

    // 친구 학습정보들
    int continueStudyDay;
    int cardStudiedCount;
}
