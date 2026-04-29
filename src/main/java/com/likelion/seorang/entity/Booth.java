package com.likelion.seorang.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "booth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Booth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 부스명
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    // 1일차 운영 여부
    @Builder.Default
    @Column(name = "day_1_open", nullable = false)
    private Boolean day1Open = false;

    // 2일차 운영 여부
    @Builder.Default
    @Column(name = "day_2_open", nullable = false)
    private Boolean day2Open = false;

    // 3일차 운영 여부
    @Builder.Default
    @Column(name = "day_3_open", nullable = false)
    private Boolean day3Open = false;
}
