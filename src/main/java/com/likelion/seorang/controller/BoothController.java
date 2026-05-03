package com.likelion.seorang.controller;

import com.likelion.seorang.common.CustomUserDetails;
import com.likelion.seorang.dto.BoothInfoResDto;
import com.likelion.seorang.dto.BoothInfoWithCheckVisitResDto;
import com.likelion.seorang.service.BoothService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 부스 정보 조회 및 방문 체크 정보 조회 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booths")
public class BoothController {
    private final BoothService boothService;

    // 부스 정보 조회
    @GetMapping
    public ResponseEntity<List<BoothInfoResDto>> getBoothsByDay(
            @RequestParam Integer day) {
        return ResponseEntity.ok(boothService.getBoothsByDay(day));
    }

    // 부스 정보와 방문 체크 정보까지 함께 조회
    @GetMapping("/visited")
    public ResponseEntity<List<BoothInfoWithCheckVisitResDto>> getBoothsWithCheckByDay(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Integer day) {
        return ResponseEntity.ok(boothService.getBoothsWithCheckVisitByDay(day, userDetails.getId()));
    }
}
