package com.pji.noticeboard.service;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.exception.ErrorCode;
import com.pji.noticeboard.exception.ServiceException;
import com.pji.noticeboard.repository.NoticeRepository;
import com.pji.noticeboard.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * NoticeService 제약사항 관련 단위 테스트 클래스입니다.
 * 테스트 목록:
 * 1. testSaveFileWithEmptyFile: 빈 파일을 저장할 때 예외가 발생하는지 테스트.
 * 2. testSaveFileWithInvalidExtension: 유효하지 않은 확장자를 가진 파일을 저장할 때 예외가 발생하는지 테스트.
 * 3. testUpdateNoticeNotFound: 존재하지 않는 공지사항을 수정할 때 예외가 발생하는지 테스트.
 * 4. testDeleteNoticeNotFound: 존재하지 않는 공지사항을 삭제할 때 예외가 발생하는지 테스트.
 */
@WithMockUser(username = "testUser")
class NoticeServiceConstraintsUnitTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * 빈 파일을 처리할 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    void testProcessFilesWithEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "file.txt", "text/plain", new byte[0]);

        doThrow(new ServiceException("Invalid file provided", ErrorCode.INVALID_FILE_PROVIDED))
                .when(fileUtil).processFiles(anyList(), eq("Test Title"));

        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(ServiceException.class, () -> noticeService.createNotice(noticeCreateDto, List.of(emptyFile)));
    }

    /**
     * 유효하지 않은 확장자를 가진 파일을 저장할 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    void testProcessFilesWithInvalidExtension() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "file.invalid", "text/plain", "content".getBytes());

        doThrow(new ServiceException("Invalid file provided", ErrorCode.INVALID_FILE_PROVIDED))
                .when(fileUtil).processFiles(anyList(), eq("Test Title"));

        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(ServiceException.class, () -> noticeService.createNotice(noticeCreateDto, List.of(invalidFile)));
    }

    /**
     * 존재하지 않는 공지사항을 수정할 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    void testUpdateNoticeNotFound() {
        NoticeUpdateDto noticeUpdateDto = NoticeUpdateDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> noticeService.updateNotice(1L, noticeUpdateDto, List.of()));
    }

    /**
     * 존재하지 않는 공지사항을 삭제할 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    void testDeleteNoticeNotFound() {
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> noticeService.deleteNotice(1L));
    }
}