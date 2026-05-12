package com.likelion.seorang.repository;

import com.likelion.seorang.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsVisitByUser_IdAndBooth_Id(Long userId, Integer boothId);

    // 방문한 부스 id 목록 조회
    @Query("select v.booth.id from Visit v where v.user.id = :userId")
    List<Integer> findVisitedBoothIdsByUserId(@Param("userId") Long userId);
}
