package com.pji.noticeboard.service;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.exception.ErrorCode;
import com.pji.noticeboard.exception.ServiceException;
import com.pji.noticeboard.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 새로운 공지사항을 등록합니다.
     *
     * @param noticeCreateDto 등록할 공지사항 정보
     * @return 등록된 공지사항
     */
    public Notice createNotice(NoticeCreateDto noticeCreateDto) {
        Notice createdNotice = Notice.builder()
                .title(noticeCreateDto.getTitle())
                .content(noticeCreateDto.getContent())
                .startDateTime(noticeCreateDto.getStartDateTime())
                .endDateTime(noticeCreateDto.getEndDateTime())
                .attachmentPaths(noticeCreateDto.getAttachmentPaths())
                .createdDate(LocalDateTime.now())
                .viewCount(0)
                .author("작성자")
                .build();

        try {
            return noticeRepository.save(createdNotice);
        } catch (Exception e) {
            throw new ServiceException("Failed to create notice", ErrorCode.NOTICE_CREATION_FAILED);
        }
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * @param id 수정할 공지사항 ID
     * @param noticeUpdateDto 수정할 공지사항 정보
     * @return 수정된 공지사항
     */
    public Notice updateNotice(Long id, NoticeUpdateDto noticeUpdateDto) {
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id " + id));
        Notice updatedNotice = existingNotice.toBuilder()
                .title(noticeUpdateDto.getTitle())
                .content(noticeUpdateDto.getContent())
                .startDateTime(noticeUpdateDto.getStartDateTime())
                .endDateTime(noticeUpdateDto.getEndDateTime())
                .attachmentPaths(noticeUpdateDto.getAttachmentPaths())
                .build();
        try {
            return noticeRepository.save(updatedNotice);
        } catch (Exception e) {
            throw new ServiceException(String.format("Failed to update notice with ID %s", id), ErrorCode.NOTICE_UPDATE_FAILED);
        }
    }

    /**
     * 공지사항을 삭제합니다.
     *
     * @param id 삭제할 공지사항 ID
     */
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id " + id));
        try {
            noticeRepository.delete(notice);
        } catch (Exception e) {
            throw new ServiceException(String.format("Failed to delete notice with ID %s", id), ErrorCode.NOTICE_DELETION_FAILED);
        }
    }

    /**
     * 특정 공지사항을 조회합니다.
     *
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항
     */
    public Notice getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id " + id));
        return notice;
    }

    /**
     * 모든 공지사항을 조회합니다.
     *
     * @return 모든 공지사항 목록
     */
    public List<Notice> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        return notices;
    }
}

