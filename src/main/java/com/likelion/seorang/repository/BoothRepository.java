package com.likelion.seorang.repository;

import com.likelion.seorang.entity.Booth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoothRepository extends JpaRepository<Booth, Integer> {

    List<Booth> findAllByDay1OpenTrue();
    List<Booth> findAllByDay2OpenTrue();
    List<Booth> findAllByDay3OpenTrue();
}
