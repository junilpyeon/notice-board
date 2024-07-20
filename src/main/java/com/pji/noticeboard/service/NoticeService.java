package com.pji.noticeboard.service;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.exception.ErrorCode;
import com.pji.noticeboard.exception.ServiceException;
import com.pji.noticeboard.repository.NoticeRepository;
import com.pji.noticeboard.util.FileExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    @Value("${file.upload.base-path}")
    private String basePath;

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

    /**
     * 파일을 저장합니다.
     * 파일의 확장자를 검증하고, 지정된 경로에 파일을 저장한 후 저장된 파일의 경로를 반환합니다.
     *
     * @param file 저장할 파일
     * @return 저장된 파일의 경로
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidExtension(originalFilename)) {
            throw new IllegalArgumentException("Invalid file type");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String dateFolder = now.format(dateFormatter);
        File uploadDir = new File(basePath, dateFolder);

        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + uploadDir.getAbsolutePath());
            }
        }

        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + originalFilename;
        File destinationFile = new File(uploadDir, fileName);

        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new IOException("Failed to save file", e);
        }

        return Paths.get(dateFolder, fileName).toString();
    }

    /**
     * 파일의 확장자가 유효한지 확인합니다.
     * 파일의 확장자를 추출하고, 허용된 확장자 목록과 비교하여 유효성을 검사합니다.
     *
     * @param filename 확장자를 확인할 파일 이름
     * @return 확장자가 유효하면 true, 그렇지 않으면 false
     */
    private boolean isValidExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return FileExtension.isValid(extension);
    }
}

