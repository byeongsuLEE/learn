package com.lbs.user.chat.dto.response;

import lombok.*;
import java.util.List;

@Data @Builder
public class ChatRoomPageResponseDto {
    private List<ChatRoomSummaryResponseDto> content;
    private long totalElements;
    private int page;
    private int size;
}
