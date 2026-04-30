package com.lbs.user.chat.dto.response;

import com.lbs.user.chat.domain.MessageType;
import com.lbs.user.chat.domain.SenderType;
import lombok.*;
import java.time.Instant;

@Data @Builder
public class ChatMessageResponseDto {
    private String id;
    private Long roomId;
    private Long senderId;
    private SenderType senderType;
    private MessageType messageType;
    private String content;
    private String imageUrl;
    private Instant createdAt;
}
