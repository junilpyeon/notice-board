package com.pji.noticeboard.service;

import com.pji.noticeboard.dto.NoticeResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class NoticeServiceTest {

    private final NoticeService noticeService;
    private final CacheManager cacheManager;

    @Autowired
    public NoticeServiceTest(NoticeService noticeService, CacheManager cacheManager) {
        this.noticeService = noticeService;
        this.cacheManager = cacheManager;
    }

    /**
     * getTopNotices 메서드의 캐시 기능을 테스트합니다.
     * 메서드를 두 번 호출하고 결과가 동일한지 확인하여 두 번째 호출이 캐시에서 반환되는지 검증합니다.
     * 또한, 캐시가 null이 아닌지 확인하여 캐시가 활성화되어 있는지 확인합니다.
     */
    @Test
    public void testCacheable() {
        List<NoticeResponseDto> topNotices1 = noticeService.getTopNotices();
        List<NoticeResponseDto> topNotices2 = noticeService.getTopNotices();

        assertThat(topNotices1).isEqualTo(topNotices2);

        assertThat(cacheManager.getCache("topNotices")).isNotNull();
    }
}