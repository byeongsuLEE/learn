package com.lbs.user.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @Builder @AllArgsConstructor
public class PresignedUrlRequestDto {
    @NotBlank(message = "fileType은 필수입니다")
    private String fileType;

    @NotNull(message = "roomId는 필수입니다")
    private Long roomId;
}
