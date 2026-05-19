package com.likelion.seorang.common;

import com.likelion.seorang.entity.Booth;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.ZoneId;

// 학과부스 운영 여부와 운영 상태를 계산하는 클래스
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BoothOperatingStatus {

    private static final LocalTime OPEN_TIME = LocalTime.of(11, 0); // 운영 시작
    private static final LocalTime CLOSE_TIME = LocalTime.of(16, 30); // 운영 종료
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul"); // 한국 시간 기준

    // 운영 날짜
    public static Boolean getDayOpen(Booth booth, Integer day) {
        return switch (day) {
            case 1 -> booth.getDay1Open();
            case 2 -> booth.getDay2Open();
            case 3 -> booth.getDay3Open();
            default -> false;
        };
    }

    // 운영 시간 반환
    public static String getOperatingTime(Boolean dayOpen) {
        if (!Boolean.TRUE.equals(dayOpen))
            return null;

        return "11:00-17:00";
    }

    // 운영 상태 반환
    public static String getOperatingStatus(Boolean dayOpen) {
        if (!Boolean.TRUE.equals(dayOpen))
            return "운영전";

        LocalTime now = LocalTime.now(KOREA_ZONE);

        boolean isOperatingTime = !now.isBefore(OPEN_TIME) && now.isBefore(CLOSE_TIME);

        if (isOperatingTime)
            return "운영중";
        return "운영전";
    }



}
