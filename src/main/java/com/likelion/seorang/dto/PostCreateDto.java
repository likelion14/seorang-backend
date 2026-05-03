package com.likelion.seorang.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PostCreateDto {
    private MultipartFile imgUrl;
    private String tag1;
    private String tag2;
    private String tag3;
}
