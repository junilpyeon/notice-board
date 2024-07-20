package com.pji.noticeboard.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoticeUpdateDto {
    private String title;
    private String content;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> attachmentPaths;
}
