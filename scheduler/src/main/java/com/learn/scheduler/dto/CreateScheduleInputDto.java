package com.learn.scheduler.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateScheduleInputDto implements Serializable {
    private String title;
    private String content;
    private Long productId;
    private String type;
}
