package com.lbs.user.chat.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class ChatRoomResponseDto {
    private Long roomId;
    private Long academyId;
    private Long parentId;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
}
