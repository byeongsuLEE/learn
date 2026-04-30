package com.lbs.user.chat.domain;

import lombok.*;
import java.time.Instant;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class ChatMessage {
    private String id;
    private Long roomId;
    private Long senderId;
    private SenderType senderType;
    private MessageType messageType;
    private String content;
    private String imageUrl;
    private Instant createdAt;
}
