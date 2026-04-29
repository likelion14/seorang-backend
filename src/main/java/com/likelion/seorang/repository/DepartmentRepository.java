package com.likelion.seorang.repository;

import com.likelion.seorang.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartName(String departmentName);
}