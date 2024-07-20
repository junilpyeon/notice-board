package com.pji.noticeboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class NoticeResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private int viewCount;
    private String author;
}
