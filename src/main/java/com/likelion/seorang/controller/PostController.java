package com.likelion.seorang.controller;

import com.likelion.seorang.common.ApiSuccess;
import com.likelion.seorang.common.CustomUserDetails;
import com.likelion.seorang.dto.LikeResponse;
import com.likelion.seorang.dto.PostAllResponse;
import com.likelion.seorang.dto.PostRequestDto;
import com.likelion.seorang.dto.PostListItemDto;
import com.likelion.seorang.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<List<PostAllResponse>> findAll(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = (user != null) ? user.getId() : null;
        List<PostAllResponse> posts = postService.findAll(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<PostListItemDto> createPost(
            @AuthenticationPrincipal CustomUserDetails user, @ModelAttribute PostRequestDto postCreateDto) {
        PostListItemDto post = postService.createPost(user.getId(), postCreateDto);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostListItemDto> updatePost(
            @AuthenticationPrincipal CustomUserDetails user,@PathVariable Long postId, @ModelAttribute PostRequestDto postUpdateDto
    ){
        PostListItemDto post = postService.updatePost(user.getId(), postId, postUpdateDto);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long postId
    ){
        postService.deletePost(user.getId(), postId);
        return ResponseEntity.status(200). body(new ApiSuccess(200, "성공적으로 처리되었습니다."));
    }

    @PatchMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> likePost(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long postId
    ){
        LikeResponse likeResponse = postService.likePost(user.getId(), postId);
        return new ResponseEntity<>(likeResponse, HttpStatus.OK);
    }
}
