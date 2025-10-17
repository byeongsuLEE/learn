package com.lbs.user.friend.dto.response;

import com.lbs.user.friend.domain.FriendRequest;
import com.lbs.user.user.infrastructure.entity.FriendRequestEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class FriendRequestResponseDto {
    String friendName;
    String friendEmail;
    Long friendId;
    public static FriendRequestResponseDto mapToResponseDto(FriendRequest friendRequest) {
        return FriendRequestResponseDto.builder()
                .friendId(friendRequest.getSenderId())
                .friendEmail(friendRequest.getFriendEmail())
                .friendName(friendRequest.getFriendName())
                .build();
    }
}
