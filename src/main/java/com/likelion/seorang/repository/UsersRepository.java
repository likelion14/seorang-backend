package com.likelion.seorang.repository;

import com.likelion.seorang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Long> {

    boolean existsByStudentId(String studentId);

    boolean existsByPhone(String phone);
}