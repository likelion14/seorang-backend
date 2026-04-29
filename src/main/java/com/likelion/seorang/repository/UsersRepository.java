package com.likelion.seorang.repository;

import com.likelion.seorang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {

    boolean existsByStudentId(String studentId);

    boolean existsByPhone(String phone);

    Optional<User> findByStudentIdAndPhone(String studentId, String phone);
}