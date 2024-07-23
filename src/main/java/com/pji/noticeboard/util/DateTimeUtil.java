package com.pji.noticeboard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * 현재 시간을 가져와 밀리초 3자리까지 포함한 LocalDateTime 객체를 반환합니다.
     *
     * @return 밀리초 3자리까지 포함한 LocalDateTime 객체
     */
    public static LocalDateTime getCurrentTimeWithMillis() {
        LocalDateTime now = LocalDateTime.now();
        int millis = now.getNano() / 1_000_000;
        String formattedMillis = String.format("%03d", millis); // 밀리초를 항상 3자리로 포맷팅
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.")) + formattedMillis;
        return LocalDateTime.parse(formattedNow, FORMATTER);
    }
}
