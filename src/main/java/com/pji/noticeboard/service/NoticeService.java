package com.pji.noticeboard.service;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeDto;
import com.pji.noticeboard.dto.NoticeResponseDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.exception.ErrorCode;
import com.pji.noticeboard.exception.ServiceException;
import com.pji.noticeboard.repository.NoticeRepository;
import com.pji.noticeboard.util.FileUtil;
import com.pji.noticeboard.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileUtil fileUtil;

    /**
     * 새로운 공지사항을 등록합니다.
     *
     * @param noticeCreateDto 등록할 공지사항 정보
     * @return 등록된 공지사항
     */
    public Notice createNotice(NoticeCreateDto noticeCreateDto, List<MultipartFile> files) {
        List<String> attachmentPaths = files != null ? fileUtil.processFiles(files, noticeCreateDto.getTitle()) : List.of();

        String currentUserName = SecurityUtil.getCurrentUserName();
        Notice createdNotice = Notice.builder()
                .title(noticeCreateDto.getTitle())
                .content(noticeCreateDto.getContent())
                .startDateTime(noticeCreateDto.getStartDateTime())
                .endDateTime(noticeCreateDto.getEndDateTime())
                .attachmentPaths(attachmentPaths)
                .createdDate(LocalDateTime.now())
                .viewCount(0)
                .author(currentUserName)
                .build();

        try {
            return noticeRepository.save(createdNotice);
        } catch (Exception e) {
            log.error("Failed to create notice", e);
            throw new ServiceException("Failed to create notice", ErrorCode.NOTICE_CREATION_FAILED, e);
        }
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * @param id 수정할 공지사항 ID
     * @param noticeUpdateDto 수정할 공지사항 정보
     * @return 수정된 공지사항
     */
    public Notice updateNotice(Long id, NoticeUpdateDto noticeUpdateDto, List<MultipartFile> files) {
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notice not found with id {}", id);
                    return new ServiceException("Notice not found with id " + id, ErrorCode.NOTICE_NOT_FOUND);
                });

        List<String> attachmentPaths = files != null ? fileUtil.processFiles(files, noticeUpdateDto.getTitle()) : List.of();

        Notice updatedNotice = existingNotice.toBuilder()
                .title(noticeUpdateDto.getTitle())
                .content(noticeUpdateDto.getContent())
                .startDateTime(noticeUpdateDto.getStartDateTime())
                .endDateTime(noticeUpdateDto.getEndDateTime())
                .attachmentPaths(attachmentPaths)
                .build();

        try {
            return noticeRepository.save(updatedNotice);
        } catch (Exception e) {
            log.error("Failed to update notice with ID {}", id, e);
            throw new ServiceException(String.format("Failed to update notice with ID %s", id), ErrorCode.NOTICE_UPDATE_FAILED, e);
        }
    }

    /**
     * 공지사항을 삭제합니다.
     *
     * @param id 삭제할 공지사항 ID
     */
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notice not found with id {}", id);
                    return new ServiceException("Notice not found with id " + id, ErrorCode.NOTICE_NOT_FOUND);
                });
        try {
            noticeRepository.delete(notice);
        } catch (Exception e) {
            log.error("Failed to delete notice with ID {}", id, e);
            throw new ServiceException(String.format("Failed to delete notice with ID %s", id), ErrorCode.NOTICE_DELETION_FAILED, e);
        }
    }

    /**
     * 특정 공지사항을 상세조회합니다.
     *
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항
     */
    public NoticeDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notice not found with id {}", id);
                    return new ServiceException("Notice not found with id " + id, ErrorCode.NOTICE_NOT_FOUND);
                });

        noticeRepository.incrementViewCount(id);

        return NoticeDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .attachmentPaths(notice.getAttachmentPaths())
                .createdDate(notice.getCreatedDate())
                .viewCount(notice.getViewCount() + 1)
                .author(notice.getAuthor())
                .build();
    }

    /**
     * 모든 공지사항을 조회합니다.
     *
     * @return 모든 공지사항 목록
     */
    public Page<NoticeResponseDto> getAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable).map(notice ->
                NoticeResponseDto.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .createdDate(notice.getCreatedDate())
                        .viewCount(notice.getViewCount())
                        .author(notice.getAuthor())
                        .build()
        );
    }

    /**
     * 조회수 상위 5개의 공지사항을 조회합니다.
     * 이 메서드는 캐시를 사용하여 성능을 최적화합니다.
     *
     * @return 조회수 상위 5개의 공지사항 목록
     */
    @Cacheable(value = "topNotices")
    public List<NoticeResponseDto> getTopNotices() {
        List<Notice> topNotices = noticeRepository.findTop5ByOrderByViewCountDesc(PageRequest.of(0, 5));
        return topNotices.stream()
                .map(notice -> NoticeResponseDto.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .createdDate(notice.getCreatedDate())
                        .viewCount(notice.getViewCount())
                        .author(notice.getAuthor())
                        .build())
                .collect(Collectors.toList());
    }
}

