package com.lbs.user.chat.domain;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor @ToString
public class ChatRoom {
    private Long id;
    private Long academyId;
    private Long parentId;
    private String lastMessage;
    private LocalDateTime lastMessageAt;

    public static ChatRoom createChatRoom(Long academyId, Long parentId) {
        return ChatRoom.builder().academyId(academyId).parentId(parentId).build();
    }
}
