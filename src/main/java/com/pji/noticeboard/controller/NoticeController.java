package com.pji.noticeboard.controller;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@Tag(name = "Notice Controller", description = "공지사항 관리 API")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 새로운 공지사항을 등록합니다.
     *
     * 파일이 포함될 경우 파일을 저장하고, 공지사항과 함께 파일 경로를 저장합니다.
     *
     * @param title 공지사항 제목
     * @param content 공지사항 내용
     * @param startDateTime 공지 시작일시
     * @param endDateTime 공지 종료일시
     * @param files 첨부 파일 목록 (선택 사항)
     * @return 등록된 공지사항
     */
    @Operation(summary = "공지사항 등록", description = "새로운 공지사항을 등록합니다.")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Notice> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @Parameter(description = "Start Date and Time", example = "2024-07-20T10:00:00")
            @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @Parameter(description = "End Date and Time", example = "2024-07-20T18:00:00")
            @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        List<String> attachmentPaths = files != null ? files.stream()
                .map(file -> {
                    try {
                        return noticeService.saveFile(file);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to save file", e);
                    }
                }).collect(Collectors.toList()) : List.of();

        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title(title)
                .content(content)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .attachmentPaths(attachmentPaths)
                .build();

        Notice createdNotice = noticeService.createNotice(noticeCreateDto);
        return ResponseEntity.ok(createdNotice);
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * 파일이 포함될 경우 파일을 저장하고, 공지사항과 함께 파일 경로를 업데이트합니다.
     *
     * @param id 수정할 공지사항 ID
     * @param title 공지사항 제목
     * @param content 공지사항 내용
     * @param startDateTime 공지 시작일시
     * @param endDateTime 공지 종료일시
     * @param files 첨부 파일 목록 (선택 사항)
     * @return 수정된 공지사항
     */
    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다.")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Notice> updateNotice(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @Parameter(description = "Start Date and Time", example = "2024-07-20T10:00:00")
            @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @Parameter(description = "End Date and Time", example = "2024-07-20T18:00:00")
            @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        List<String> attachmentPaths = files != null ? files.stream()
                .map(file -> {
                    try {
                        return noticeService.saveFile(file);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to save file", e);
                    }
                }).collect(Collectors.toList()) : List.of();

        NoticeUpdateDto noticeUpdateDto = NoticeUpdateDto.builder()
                .title(title)
                .content(content)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .attachmentPaths(attachmentPaths)
                .build();

        Notice updatedNotice = noticeService.updateNotice(id, noticeUpdateDto);
        return ResponseEntity.ok(updatedNotice);
    }

    /**
     * 공지사항을 삭제합니다.
     *
     * @param id 삭제할 공지사항 ID
     * @return 삭제 결과
     */
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 공지사항을 조회합니다.
     *
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항
     */
    @Operation(summary = "공지사항 조회", description = "특정 공지사항을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long id) {
        Notice notice = noticeService.getNotice(id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 모든 공지사항을 조회합니다.
     *
     * @return 모든 공지사항 목록
     */
    @Operation(summary = "공지사항 목록 조회", description = "모든 공지사항을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }
}
