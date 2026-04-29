package com.likelion.seorang.entity;

import com.likelion.seorang.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 학번
    @Column(name = "student_id", length = 10, nullable = false, unique = true)
    private String studentId;

    // 이름
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    // 전화번호
    @Column(name = "phone", length = 50, nullable = false, unique = true)
    private String phone;

    // 학과
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id", nullable = false)
    private Department department;

    // 최근 방문 페이지
    @Column(name = "recent_page", length = 200)
    private String recentPage;

    // 운영진 or 일반 학우
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 회원가입 시각
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.role == null) {
            this.role = Role.STUDENT;
        }
    }

    public void updateRecentPage(String recentPage) {
        this.recentPage = recentPage;
    }
}
