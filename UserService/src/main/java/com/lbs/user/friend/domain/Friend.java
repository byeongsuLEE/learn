package com.lbs.user.friend.domain;

import com.lbs.user.friend.dto.response.FriendResponseDto;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-12
 * 풀이방법
 **/


@Builder
@Getter
public class Friend {

    private Long id;
    private User friendUser;

    public FriendResponseDto mapToResponseDto(Friend friend) {
        return FriendResponseDto.builder()
                .email(friend.friendUser.getEmail())
                .name(friend.friendUser.getName())
                .build();
//                .cardStudiedCount() // 나중 추가
//                .continueStudyDay()
    }
}
