package com.lbs.user.chat.dto.response;

import com.lbs.user.chat.domain.SenderType;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class ChatRoomSummaryResponseDto {
    private Long roomId;
    private Long counterpartId;
    private SenderType counterpartType;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
}
