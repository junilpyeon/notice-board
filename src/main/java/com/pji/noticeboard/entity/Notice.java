package com.pji.noticeboard.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
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

    public Notice() {}

    public Notice(Long id, String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime,
                  List<String> attachmentPaths, LocalDateTime createdDate, int viewCount, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.attachmentPaths = attachmentPaths;
        this.createdDate = createdDate != null ? createdDate : LocalDateTime.now();
        this.viewCount = viewCount;
        this.author = author;
    }
}
