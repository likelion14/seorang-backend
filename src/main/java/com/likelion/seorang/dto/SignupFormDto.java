package com.likelion.seorang.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupFormDto {

    // 학번
    @NotBlank(message = "학번은 필수 입력 값입니다.")
    private String studentId;

    // 이름
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    // 전화번호
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    // 학과 이름
    @NotBlank(message = "학과는 필수 입력 값입니다.")
    private String departmentName;
}