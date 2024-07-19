package com.pji.noticeboard.controller;

import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@Tag(name = "Notice Controller", description = "공지사항 관리 API")
public class NoticeController {

    private NoticeService noticeService;

    /**
     * 새로운 공지사항을 등록합니다.
     *
     * @param notice 등록할 공지사항 정보
     * @return 등록된 공지사항
     */
    @Operation(summary = "공지사항 등록", description = "새로운 공지사항을 등록합니다.")
    @PostMapping
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice) {
        Notice createdNotice = noticeService.createNotice(notice);
        return ResponseEntity.ok(createdNotice);
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * @param id     수정할 공지사항 ID
     * @param notice 수정할 공지사항 정보
     * @return 수정된 공지사항
     */
    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Notice> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        Notice updatedNotice = noticeService.updateNotice(id, notice);
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
