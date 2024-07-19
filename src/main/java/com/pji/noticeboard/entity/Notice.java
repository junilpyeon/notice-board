package com.pji.noticeboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ElementCollection
    private List<String> attachmentPaths;

    private LocalDateTime createdDate;
    private int viewCount;
    private String author;
}
