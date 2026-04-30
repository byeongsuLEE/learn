package com.lbs.user.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @Builder @AllArgsConstructor
public class CreateChatRoomRequestDto {
    @NotNull(message = "academyId는 필수입니다")
    private Long academyId;

    @NotNull(message = "parentId는 필수입니다")
    private Long parentId;
}
