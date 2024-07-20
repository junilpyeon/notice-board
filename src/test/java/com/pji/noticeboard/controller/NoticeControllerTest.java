package com.pji.noticeboard.controller;

import com.pji.noticeboard.dto.NoticeCreateDto;
import com.pji.noticeboard.entity.Notice;
import com.pji.noticeboard.repository.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NoticeController의 통합 테스트 클래스입니다.
 *
 * 테스트 목록:
 * 1. testCreateNotice: 공지사항을 생성하는 테스트.
 * 2. testGetNotice: ID로 공지사항을 조회하는 테스트.
 * 3. testUpdateNotice: 공지사항을 업데이트하는 테스트.
 * 4. testDeleteNotice: 공지사항을 삭제하는 테스트.
 * 5. testGetTopNotices: 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoticeRepository noticeRepository;

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
    }

    /**
     * 공지사항을 생성하는 테스트.
     * - 공지사항을 생성하고, 생성된 공지사항의 상태 코드를 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void testCreateNotice() throws Exception {
        NoticeCreateDto noticeCreateDto = NoticeCreateDto.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .attachmentPaths(List.of())
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes()
        );

        mockMvc.perform(multipart("/api/notices")
                        .file(file)
                        .param("title", noticeCreateDto.getTitle())
                        .param("content", noticeCreateDto.getContent())
                        .param("startDateTime", noticeCreateDto.getStartDateTime().toString())
                        .param("endDateTime", noticeCreateDto.getEndDateTime().toString()))
                .andExpect(status().isOk());
    }

    /**
     * ID로 공지사항을 조회하는 테스트.
     * - 공지사항을 저장한 후 해당 공지사항을 ID로 조회하여 결과를 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void testGetNotice() throws Exception {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        notice = noticeRepository.save(notice);

        mockMvc.perform(get("/api/notices/" + notice.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    /**
     * 공지사항을 업데이트하는 테스트.
     * - 공지사항을 저장한 후 업데이트하고, 업데이트된 결과를 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void testUpdateNotice() throws Exception {
        Notice notice = Notice.builder()
                .title("Old Title")
                .content("Old Content")
                .startDateTime(LocalDateTime.now().minusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        notice = noticeRepository.save(notice);

        MockMultipartFile file = new MockMultipartFile("files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/notices/" + notice.getId())
                        .file(file)
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .param("startDateTime", LocalDateTime.now().toString())
                        .param("endDateTime", LocalDateTime.now().plusDays(1).toString())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    /**
     * 공지사항을 삭제하는 테스트.
     * - 공지사항을 저장한 후 삭제하고, 삭제된 공지사항이 존재하지 않는지 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void testDeleteNotice() throws Exception {
        Notice notice = Notice.builder()
                .title("Test Title")
                .content("Test Content")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(1))
                .viewCount(0)
                .createdDate(LocalDateTime.now())
                .author("Author")
                .build();

        notice = noticeRepository.save(notice);

        mockMvc.perform(delete("/api/notices/" + notice.getId()))
                .andExpect(status().isNoContent());

        assertFalse(noticeRepository.existsById(notice.getId()));
    }

    /**
     * 조회수 기준 상위 5개의 공지사항을 조회하는 테스트.
     * - 여러 공지사항을 저장한 후 조회수 기준 상위 5개의 공지사항을 조회하여 결과를 검증합니다.
     */
    @Test
    @WithMockUser(username = "testUser")
    void testGetTopNotices() throws Exception {
        Notice notice1 = Notice.builder().title("Notice 1").viewCount(100).build();
        Notice notice2 = Notice.builder().title("Notice 2").viewCount(80).build();
        Notice notice3 = Notice.builder().title("Notice 3").viewCount(60).build();
        Notice notice4 = Notice.builder().title("Notice 4").viewCount(40).build();
        Notice notice5 = Notice.builder().title("Notice 5").viewCount(20).build();
        Notice notice6 = Notice.builder().title("Notice 6").viewCount(10).build();

        noticeRepository.saveAll(List.of(notice1, notice2, notice3, notice4, notice5, notice6));

        mockMvc.perform(get("/api/notices/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Notice 1"))
                .andExpect(jsonPath("$[4].title").value("Notice 5"));
    }
}
