package com.likelion.seorang.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecentPageFormDto {
    @NotBlank(message = "url은 필수 입력 값입니다.")
    @Size(max = 200, message = "url은 최대 200자까지 가능합니다.")
    private String url;
}
