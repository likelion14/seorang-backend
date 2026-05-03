package com.likelion.seorang.controller;

import com.likelion.seorang.common.ApiSuccess;
import com.likelion.seorang.common.CustomUserDetails;
import com.likelion.seorang.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    @PostMapping("/{boothId}")
    public ResponseEntity<?> checkVisit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer boothId) {
        visitService.checkVisit(userDetails.getId(), boothId);

        return ResponseEntity.status(200).body(new ApiSuccess(200, "성공적으로 처리되었습니다."));
    }
}
