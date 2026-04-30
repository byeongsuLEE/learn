package com.lbs.user.chat.dto.response;

import lombok.*;
import java.util.List;

@Data @Builder
public class ChatMessagePageResponseDto {
    private List<ChatMessageResponseDto> content;
    private String nextCursor;
    private boolean hasNext;
}
