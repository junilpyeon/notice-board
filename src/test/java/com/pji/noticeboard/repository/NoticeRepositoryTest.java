package com.pji.noticeboard.repository;

import com.pji.noticeboard.entity.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NoticeRepository 통합 단위 테스트 클래스입니다.
 * 테스트 목록:
 * 1. testFindById: ID로 공지사항을 조회하는 테스트.
 * 2. testSave: 공지사항을 저장하는 테스트.
 * 3. testDelete: 공지사항을 삭제하는 테스트.
 * 4. testFindTop5ByOrderByViewCountDesc: 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
 * 5. testFindAll: 모든 공지사항을 페이징하여 조회하는 테스트.
 * 6. testIncrementViewCount: 조회수를 증가시키는 메서드의 테스트.
 */
@DataJpaTest
@ActiveProfiles("test")
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
    }

    /**
     * ID로 공지사항을 조회하는 테스트.
     * - 공지사항을 저장하고, 해당 공지사항을 ID로 조회하여 검증합니다.
     */
    @Test
    void testFindById() {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        Notice foundNotice = noticeRepository.findById(savedNotice.getId()).orElse(null);

        assertNotNull(foundNotice);
        assertEquals(savedNotice.getId(), foundNotice.getId());
    }

    /**
     * 공지사항을 저장하는 테스트.
     * - 공지사항을 저장하고, 저장된 공지사항의 제목을 검증합니다.
     */
    @Test
    void testSave() {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        assertNotNull(savedNotice);
        assertEquals("Test Title", savedNotice.getTitle());
    }

    /**
     * 공지사항을 삭제하는 테스트.
     * - 공지사항을 저장하고, 삭제한 후 해당 공지사항이 존재하지 않는지 검증합니다.
     */
    @Test
    void testDelete() {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        noticeRepository.delete(savedNotice);

        Notice foundNotice = noticeRepository.findById(savedNotice.getId()).orElse(null);
        assertNull(foundNotice);
    }

    /**
     * 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
     * - 여러 공지사항을 저장한 후, 조회수를 기준으로 상위 5개를 조회하여 검증합니다.
     */
    @Test
    void testFindTop5ByOrderByViewCountDesc() {
        Notice notice1 = Notice.builder().title("Notice 1").viewCount(100).build();
        Notice notice2 = Notice.builder().title("Notice 2").viewCount(80).build();
        Notice notice3 = Notice.builder().title("Notice 3").viewCount(60).build();
        Notice notice4 = Notice.builder().title("Notice 4").viewCount(40).build();
        Notice notice5 = Notice.builder().title("Notice 5").viewCount(20).build();
        Notice notice6 = Notice.builder().title("Notice 6").viewCount(10).build();

        noticeRepository.saveAll(List.of(notice1, notice2, notice3, notice4, notice5, notice6));

        List<Notice> topNotices = noticeRepository.findTop5ByOrderByViewCountDesc(PageRequest.of(0, 5));

        assertEquals(5, topNotices.size());
        assertEquals("Notice 1", topNotices.get(0).getTitle());
        assertEquals("Notice 5", topNotices.get(4).getTitle());
    }

    /**
     * 모든 공지사항을 페이징하여 조회하는 테스트.
     * - 여러 공지사항을 저장한 후, 페이징하여 조회하고 결과를 검증합니다.
     */
    @Test
    void testFindAll() {
        Notice notice1 = Notice.builder().title("Notice 1").viewCount(100).build();
        Notice notice2 = Notice.builder().title("Notice 2").viewCount(80).build();
        noticeRepository.saveAll(List.of(notice1, notice2));

        Page<Notice> noticesPage = noticeRepository.findAll(PageRequest.of(0, 10));

        assertEquals(2, noticesPage.getTotalElements());
        assertEquals("Notice 1", noticesPage.getContent().get(0).getTitle());
    }

    /**
     * 조회수를 증가시키는 메서드의 테스트.
     * - 공지사항을 저장하고, 조회수를 증가시킨 후 증가된 조회수를 검증합니다.
     */
    @Test
    @Transactional
    void testIncrementViewCount() {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        noticeRepository.incrementViewCount(savedNotice.getId());

        entityManager.flush();
        entityManager.clear();

        Notice updatedNotice = noticeRepository.findById(savedNotice.getId()).orElse(null);

        assertNotNull(updatedNotice);
        assertEquals(1, updatedNotice.getViewCount());
    }
}
