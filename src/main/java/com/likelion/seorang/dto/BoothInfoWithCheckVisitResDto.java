package com.likelion.seorang.dto;

import com.likelion.seorang.common.BoothOperatingStatus;
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
    private String operatingTime; // 운영 시각
    private String operatingStatus; // 운영 상태
    private Boolean visited; // 방문 체크 여부

    public static BoothInfoWithCheckVisitResDto from(Booth booth, Integer day, boolean visited) {
        Boolean dayOpen = BoothOperatingStatus.getDayOpen(booth, day);

        return BoothInfoWithCheckVisitResDto.builder()
                .id(booth.getId())
                .name(booth.getName())
                .dayOpen(dayOpen)
                .operatingTime(BoothOperatingStatus.getOperatingTime(dayOpen))
                .operatingStatus(BoothOperatingStatus.getOperatingStatus(dayOpen))
                .visited(visited)
                .build();
    }
}
