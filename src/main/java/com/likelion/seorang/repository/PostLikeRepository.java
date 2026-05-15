package com.likelion.seorang.repository;

import com.likelion.seorang.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    void deleteByPost_IdAndUser_Id(Long postId, Long userId);

    void deleteByPost_Id(Long postId);

    @Query("""
        select pl.post.id
        from PostLike pl
        where pl.user.id = :userId
    """)
    List<Long> findLikedPostIds(@Param("userId") Long userId);

    int countByPost_Id(Long postId);
}
