package com.pji.noticeboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class NoticeDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> attachmentPaths;
    private LocalDateTime createdDate;
    private int viewCount;
    private String author;
    }
