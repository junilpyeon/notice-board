package com.pji.noticeboard.service;

import com.pji.noticeboard.config.SecurityConfig;
import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeDto;
import com.pji.noticeboard.dto.NoticeResponseDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.repository.NoticeRepository;
import com.pji.noticeboard.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * NoticeService 단위 테스트 클래스입니다.
 * 테스트 목록:
 * 1. unitTestGetNotice(): ID로 공지사항을 조회하는 기능을 테스트.
 * 2. unitTestCreateNotice(): 공지사항을 생성하는 기능을 테스트.
 * 3. unitTestUpdateNotice(): 공지사항을 업데이트하는 기능을 테스트.
 * 4. unitTestDeleteNotice(): 공지사항을 삭제하는 기능을 테스트.
 * 5. unitTestGetTopNotices(): 조회수 기준 상위 5개의 공지사항을 조회하는 기능을 테스트.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
class NoticeServiceUnitTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private NoticeService noticeService;

    @BeforeEach
    void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * ID로 공지사항을 조회하는 테스트.
     * - 공지사항을 저장하고, 해당 공지사항을 ID로 조회하여 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void unitTestGetNotice() {
        Notice notice = Notice.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        NoticeDto noticeDto = noticeService.getNotice(1L);

        assertNotNull(noticeDto);
        assertEquals("Test Title", noticeDto.getTitle());
        assertEquals(1, noticeDto.getViewCount());
    }

    /**
     * 공지사항을 생성하는 테스트.
     * - 공지사항을 생성하고, 저장된 공지사항의 제목을 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void unitTestCreateNotice() {
        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title("New Title")
                .content("New Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .attachmentPaths(List.of())
                .build();

        List<MultipartFile> files = List.of(
                new MockMultipartFile("file1", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 1 Content".getBytes()),
                new MockMultipartFile("file2", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 2 Content".getBytes())
        );

        Notice notice = Notice.builder()
                .title("New Title")
                .content("New Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .author("testUser")
                .build();

        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        doAnswer(invocation -> {
            List<MultipartFile> providedFiles = invocation.getArgument(0);
            String providedTitle = invocation.getArgument(1);

            assertEquals(2, providedFiles.size());
            assertEquals("New Title", providedTitle);
            return null;
        }).when(fileUtil).processFiles(files, noticeCreateDto.getTitle());

        Notice createdNotice = noticeService.createNotice(noticeCreateDto, files);

        assertNotNull(createdNotice);
        assertEquals("New Title", createdNotice.getTitle());
        assertEquals("testUser", createdNotice.getAuthor());
    }

    /**
     * 공지사항을 업데이트하는 테스트.
     * - 공지사항을 업데이트하고, 업데이트된 공지사항의 제목을 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void unitTestUpdateNotice() {
        Notice existingNotice = Notice.builder()
                .id(1L) // 명시적으로 ID 설정
                .title("Old Title")
                .content("Old Content")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        NoticeUpdateDto noticeUpdateDto = NoticeUpdateDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .attachmentPaths(List.of())
                .build();

        List<MultipartFile> files = List.of(
                new MockMultipartFile("file1", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 1 Content".getBytes()),
                new MockMultipartFile("file2", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 2 Content".getBytes())
        );

        when(noticeRepository.findById(1L)).thenReturn(Optional.of(existingNotice));
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> {
            Notice notice = invocation.getArgument(0);
            notice.setTitle(noticeUpdateDto.getTitle());
            return notice;
        });

        Notice updatedNotice = noticeService.updateNotice(1L, noticeUpdateDto, files);

        assertNotNull(updatedNotice);
        assertEquals("Updated Title", updatedNotice.getTitle());
    }

    /**
     * 공지사항을 삭제하는 테스트.
     * - 공지사항을 삭제하고, 삭제 메서드가 호출되었는지 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void unitTestDeleteNotice() {
        Notice notice = Notice.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));
        doNothing().when(noticeRepository).delete(any(Notice.class));

        noticeService.deleteNotice(notice.getId());

        verify(noticeRepository, times(1)).findById(notice.getId());
        verify(noticeRepository, times(1)).delete(notice);
    }

    /**
     * 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
     * - 여러 공지사항을 저장한 후, 조회수를 기준으로 상위 5개를 조회하여 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void unitTestGetTopNotices() {
        List<Notice> topNotices = List.of(
                Notice.builder().title("Notice 1").viewCount(100).build(),
                Notice.builder().title("Notice 2").viewCount(80).build(),
                Notice.builder().title("Notice 3").viewCount(60).build(),
                Notice.builder().title("Notice 4").viewCount(40).build(),
                Notice.builder().title("Notice 5").viewCount(20).build()
        );

        when(noticeRepository.findTop5ByOrderByViewCountDesc(PageRequest.of(0, 5))).thenReturn(topNotices);

        List<NoticeResponseDto> topNoticesDto = noticeService.getTopNotices();

        assertNotNull(topNoticesDto);
        assertEquals(5, topNoticesDto.size());
        assertEquals("Notice 1", topNoticesDto.get(0).getTitle());
    }
}
