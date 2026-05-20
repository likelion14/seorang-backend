package com.likelion.seorang.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 PK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 이미지 경로, S3 URL
    @Column(name = "img_url", length = 500, nullable = false)
    private String imgUrl;

    // 태그1
    @Column(name = "tag_1", length = 10, nullable = false)
    private String tag1;

    // 태그2
    @Column(name = "tag_2", length = 10)
    private String tag2;

    // 태그3
    @Column(name = "tag_3", length = 10)
    private String tag3;

    // 좋아요 개수
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    // 게시글 등록 시각
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 게시글 수정 시각
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.likeCount == null) {
            this.likeCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePost(String imgUrl, String tag1, String tag2, String tag3) {
        this.imgUrl = imgUrl;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
    }

    public Integer increaseLikeCount() {
        this.likeCount++;
        return this.likeCount;
    }

    public Integer decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
            return this.likeCount;
        }
        return 0;
    }

    public void updateLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
