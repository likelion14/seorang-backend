package com.likelion.seorang.repository;

import com.likelion.seorang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


// 유저 레포지토리
public interface UsersRepository extends JpaRepository<User, Long> {
    // 유저 아이디 중복 검사
    boolean existsById(String id);

    // 유저 아이디로 해당 유저 찾기
    Optional<User> findById(String id);
}
