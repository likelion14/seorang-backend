package com.likelion.seorang.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequestDto {
    private MultipartFile imgUrl;
    private String tag1;
    private String tag2;
    private String tag3;
}
