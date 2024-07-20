package com.pji.noticeboard.controller;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeDto;
import com.pji.noticeboard.dto.NoticeResponseDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.exception.ErrorResponse;
import com.pji.noticeboard.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

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
    @Operation(summary = "공지사항 등록", description = "새로운 공지사항을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항이 성공적으로 등록됨", content = @Content(schema = @Schema(implementation = Notice.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Notice> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @Parameter(description = "Start Date and Time", example = "2024-07-20T10:00:00")
            @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @Parameter(description = "End Date and Time", example = "2024-07-20T18:00:00")
            @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title(title)
                .content(content)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        Notice createdNotice = noticeService.createNotice(noticeCreateDto, files);
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
    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항이 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = Notice.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
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
        NoticeUpdateDto noticeUpdateDto = NoticeUpdateDto.builder()
                .title(title)
                .content(content)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        Notice updatedNotice = noticeService.updateNotice(id, noticeUpdateDto, files);
        return ResponseEntity.ok(updatedNotice);
    }

    /**
     * 공지사항을 삭제합니다.
     *
     * @param id 삭제할 공지사항 ID
     * @return 삭제 결과
     */
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "공지사항이 성공적으로 삭제됨"),
                    @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 공지사항을 상세조회합니다.
     *
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항
     */
    @Operation(summary = "공지사항 상세조회", description = "특정 공지사항을 상세조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 상세 조회 성공", content = @Content(schema = @Schema(implementation = NoticeDto.class))),
                    @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDto> getNotice(@PathVariable Long id) {
        NoticeDto notice = noticeService.getNotice(id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 모든 공지사항을 조회합니다.
     *
     * @return 페이징된 공지사항 목록
     */
    @Operation(summary = "공지사항 목록 조회", description = "모든 공지사항을 페이징하여 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공", content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping
    public ResponseEntity<Page<NoticeResponseDto>> getAllNotices(
            @Parameter(description = "페이징 및 정렬 정보. 예시: ?page=0&size=10&sort=createdDate,desc")
            @PageableDefault(size = 10) Pageable pageable) {
        Page<NoticeResponseDto> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(notices);
    }

    /**
     * 조회수 상위 5개 공지사항을 조회합니다.
     * 이 엔드포인트는 조회수가 가장 높은 5개의 공지사항을 반환합니다.
     * 공지사항은 조회수 기준으로 내림차순 정렬되어 반환됩니다.
     *
     * @return 조회수 상위 5개 공지사항 목록
     */
    @Operation(summary = "조회수 상위 5개 공지사항 조회", description = "조회수가 가장 높은 5개의 공지사항을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회수 상위 공지사항 목록 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/top")
    public ResponseEntity<List<NoticeResponseDto>> getTopNotices() {
        List<NoticeResponseDto> topNotices = noticeService.getTopNotices();
        return ResponseEntity.ok(topNotices);
    }
}
