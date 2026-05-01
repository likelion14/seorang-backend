package com.likelion.seorang.dto;

import com.likelion.seorang.entity.Booth;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class BoothInfoResDto implements Serializable {
    private String name;
    private Boolean dayOpen; // 요청한 날짜의 운영 여부만 내려줌

    public static BoothInfoResDto from(Booth booth, Integer day) {
        Boolean dayOpen = switch (day) {
            case 1 -> booth.getDay1Open();
            case 2 -> booth.getDay2Open();
            case 3 -> booth.getDay3Open();
            default -> false;
        };

        return BoothInfoResDto.builder()
                .name(booth.getName())
                .dayOpen(dayOpen)
                .build();
    }
}
