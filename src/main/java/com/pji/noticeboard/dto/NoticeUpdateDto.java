package com.pji.noticeboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeUpdateDto {

    @NotNull(message = "Title is required")
    @Size(max = 100, message = "Title can be up to 100 characters long")
    @Schema(description = "Title of the notice", example = "Meeting Announcement")
    private String title;

    @NotNull(message = "Content is required")
    @Size(max = 1000, message = "Content can be up to 1000 characters long")
    @Schema(description = "Content of the notice", example = "We will have a meeting at 10 AM")
    private String content;

    @NotNull(message = "Start date and time is required")
    @Schema(description = "Start Date and Time", example = "2024-07-20T10:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    @Future(message = "End date and time must be in the future")
    @Schema(description = "End Date and Time", example = "2024-07-20T18:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;
}
