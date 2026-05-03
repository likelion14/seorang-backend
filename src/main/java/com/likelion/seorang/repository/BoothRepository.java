package com.likelion.seorang.repository;

import com.likelion.seorang.entity.Booth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoothRepository extends JpaRepository<Booth, Integer> {

    @Query("select b from Booth b where " +
            "(:day = 1 and b.day1Open = true) or " +
            "(:day = 2 and b.day2Open = true) or " +
            "(:day = 3 and b.day3Open = true)")
    List<Booth> findAllBy(@Param("day") Integer day);
}
