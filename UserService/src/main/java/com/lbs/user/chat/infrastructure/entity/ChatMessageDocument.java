package com.lbs.user.chat.infrastructure.entity;

import com.lbs.user.chat.domain.MessageType;
import com.lbs.user.chat.domain.SenderType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
@CompoundIndex(def = "{'roomId': 1, 'createdAt': -1}")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ChatMessageDocument {

    @Id
    private String id;

    private Long roomId;
    private Long senderId;
    private SenderType senderType;
    private MessageType messageType;
    private String content;
    private String imageUrl;

    @CreatedDate
    private Instant createdAt;
}
