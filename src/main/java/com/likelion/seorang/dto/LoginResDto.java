package com.likelion.seorang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResDto {
    private String accessToken;
    private String refreshToken;
    private String recentPage;
}
