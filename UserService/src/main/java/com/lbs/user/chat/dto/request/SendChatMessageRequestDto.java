package com.lbs.user.chat.dto.request;

import com.lbs.user.chat.domain.MessageType;
import lombok.*;

@Getter @Setter @NoArgsConstructor @Builder @AllArgsConstructor
public class SendChatMessageRequestDto {
    private MessageType messageType;
    private String content;
    private String imageUrl;
}
