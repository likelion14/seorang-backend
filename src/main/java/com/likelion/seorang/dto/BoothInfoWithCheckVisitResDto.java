package com.likelion.seorang.dto;

import com.likelion.seorang.entity.Booth;
import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoothInfoWithCheckVisitResDto implements Serializable {
    private Integer id;
    private String name;
    private Boolean dayOpen; // 요청한 날짜의 운영 여부만 내려줌
    private Boolean visited; // 방문 체크 여부

    public static BoothInfoWithCheckVisitResDto from(Booth booth, Integer day, boolean visited) {
        Boolean dayOpen = switch (day) {
            case 1 -> booth.getDay1Open();
            case 2 -> booth.getDay2Open();
            case 3 -> booth.getDay3Open();
            default -> false;
        };

        return BoothInfoWithCheckVisitResDto.builder()
                .id(booth.getId())
                .name(booth.getName())
                .dayOpen(dayOpen)
                .visited(visited)
                .build();
    }
}
