package com.likelion.seorang.dto;

import com.likelion.seorang.common.BoothOperatingStatus;
import com.likelion.seorang.entity.Booth;
import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 추가
@AllArgsConstructor // @Builder + @NoArgsConstructor 함께 쓸 때 필요
public class BoothInfoResDto implements Serializable {
    private Integer id;
    private String name;
    private Boolean dayOpen; // 요청한 날짜의 운영 여부
    private String operatingTime; // 운영 시각
    private String operatingStatus; // 운영 상태

    public static BoothInfoResDto from(Booth booth, Integer day) {
        Boolean dayOpen = BoothOperatingStatus.getDayOpen(booth, day);

        return BoothInfoResDto.builder()
                .id(booth.getId())
                .name(booth.getName())
                .dayOpen(dayOpen)
                .operatingTime(BoothOperatingStatus.getOperatingTime(dayOpen))
                .operatingStatus(BoothOperatingStatus.getOperatingStatus(dayOpen))
                .build();
    }
}
