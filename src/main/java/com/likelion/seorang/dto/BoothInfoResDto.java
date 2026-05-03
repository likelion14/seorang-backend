package com.likelion.seorang.dto;

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
    private Boolean dayOpen; // 요청한 날짜의 운영 여부만 내려줌

    public static BoothInfoResDto from(Booth booth, Integer day) {
        Boolean dayOpen = switch (day) {
            case 1 -> booth.getDay1Open();
            case 2 -> booth.getDay2Open();
            case 3 -> booth.getDay3Open();
            default -> false;
        };

        return BoothInfoResDto.builder()
                .id(booth.getId())
                .name(booth.getName())
                .dayOpen(dayOpen)
                .build();
    }
}
