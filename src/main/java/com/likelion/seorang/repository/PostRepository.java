package com.likelion.seorang.repository;

import com.likelion.seorang.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
