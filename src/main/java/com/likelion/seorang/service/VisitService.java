package com.likelion.seorang.service;

import com.likelion.seorang.entity.Booth;
import com.likelion.seorang.entity.User;
import com.likelion.seorang.entity.Visit;
import com.likelion.seorang.repository.BoothRepository;
import com.likelion.seorang.repository.UsersRepository;
import com.likelion.seorang.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitService {
    private final VisitRepository visitRepository;
    private final UsersRepository usersRepository;
    private final BoothRepository boothRepository;

    public void checkVisit(Long userId, Integer boothId) {
        // 중복 방문 체크
        if (visitRepository.existsVisitByUser_IdAndBooth_Id(userId, boothId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ALREADY_VISITED");
        }

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND"));

        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "BOOTH_NOT_FOUND"));

        visitRepository.save(Visit.of(user, booth));
    }
}
