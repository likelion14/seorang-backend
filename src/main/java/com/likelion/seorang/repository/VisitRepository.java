package com.likelion.seorang.repository;

import com.likelion.seorang.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsVisitByUserIdAndBoothId(Long userId, Integer boothId);
}
