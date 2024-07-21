package com.pji.noticeboard.service;

import com.pji.noticeboard.config.SecurityConfig;
import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.dto.NoticeDto;
import com.pji.noticeboard.dto.NoticeResponseDto;
import com.pji.noticeboard.dto.NoticeUpdateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NoticeService 통합 기능 테스트 클래스입니다.
 * 테스트 목록:
 * 1. integrationTestGetNotice: ID로 공지사항을 조회하는 테스트.
 * 2. integrationTestCreateNotice: 공지사항을 생성하는 테스트.
 * 3. integrationTestUpdateNotice: 공지사항을 업데이트하는 테스트.
 * 4. integrationTestDeleteNotice: 공지사항을 삭제하는 테스트.
 * 5. integrationTestGetTopNotices: 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
 * 6. integrationTestCacheable: getTopNotices 메서드의 캐시 기능을 테스트하는 테스트.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(SecurityConfig.class)
class NoticeServiceIntegrationTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeRepository.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("topNotices")).clear();
    }

    /**
     * ID로 공지사항을 조회하는 테스트.
     * - 공지사항을 저장하고, 해당 공지사항을 ID로 조회하여 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void integrationTestGetNotice() {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        noticeRepository.save(notice);

        NoticeDto noticeDto = noticeService.getNotice(notice.getId());

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
    void integrationTestCreateNotice() {
        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title("New Title")
                .content("New Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        List<MultipartFile> files = List.of(
                new MockMultipartFile("file1", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 1 Content".getBytes()),
                new MockMultipartFile("file2", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 2 Content".getBytes())
        );

        Notice createdNotice = noticeService.createNotice(noticeCreateDto, files);

        assertNotNull(createdNotice);
        assertEquals("New Title", createdNotice.getTitle());
        assertEquals("testUser", createdNotice.getAuthor());
        assertNotNull(createdNotice.getId());
    }

    /**
     * 공지사항을 업데이트하는 테스트.
     * - 공지사항을 업데이트하고, 업데이트된 공지사항의 제목을 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void integrationTestUpdateNotice() {
        Notice existingNotice = Notice.builder()
                .title("Old Title")
                .content("Old Content")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        existingNotice = noticeRepository.save(existingNotice); // 데이터베이스에 저장

        NoticeUpdateDto noticeUpdateDto = NoticeUpdateDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .build();

        List<MultipartFile> files = List.of(
                new MockMultipartFile("file1", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 1 Content".getBytes()),
                new MockMultipartFile("file2", "file2.txt", MediaType.TEXT_PLAIN_VALUE, "Test File 2 Content".getBytes())
        );

        Notice updatedNotice = noticeService.updateNotice(existingNotice.getId(), noticeUpdateDto, files);

        assertNotNull(updatedNotice);
        assertEquals("Updated Title", updatedNotice.getTitle());
    }

    /**
     * 공지사항을 삭제하는 테스트.
     * - 공지사항을 삭제하고, 삭제 메서드가 호출되었는지 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void integrationTestDeleteNotice() {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        notice = noticeRepository.save(notice); // 데이터베이스에 저장

        noticeService.deleteNotice(notice.getId());

        assertFalse(noticeRepository.findById(notice.getId()).isPresent());
    }

    /**
     * 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
     * - 여러 공지사항을 저장한 후, 조회수를 기준으로 상위 5개를 조회하여 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void integrationTestGetTopNotices() {
        List<Notice> topNotices = List.of(
                Notice.builder().title("Notice 1").viewCount(100).build(),
                Notice.builder().title("Notice 2").viewCount(80).build(),
                Notice.builder().title("Notice 3").viewCount(60).build(),
                Notice.builder().title("Notice 4").viewCount(40).build(),
                Notice.builder().title("Notice 5").viewCount(20).build()
        );

        noticeRepository.saveAll(topNotices); // 데이터베이스에 저장

        List<NoticeResponseDto> topNoticesDto = noticeService.getTopNotices();

        assertNotNull(topNoticesDto);
        assertEquals(5, topNoticesDto.size());
        assertEquals("Notice 1", topNoticesDto.get(0).getTitle());
    }

    /**
     * getTopNotices 메서드의 캐시 기능을 테스트합니다.
     * 메서드를 두 번 호출하고 결과가 동일한지 확인하여 두 번째 호출이 캐시에서 반환되는지 검증합니다.
     * 또한, 캐시가 null이 아닌지 확인하여 캐시가 활성화되어 있는지 확인합니다.
     */
    @Test
    void integrationTestCacheable() {
        List<NoticeResponseDto> topNotices1 = noticeService.getTopNotices();
        List<NoticeResponseDto> topNotices2 = noticeService.getTopNotices();

        assertThat(topNotices1).isEqualTo(topNotices2);
        assertThat(cacheManager.getCache("topNotices")).isNotNull();
    }
}