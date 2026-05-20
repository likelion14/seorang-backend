package com.likelion.seorang.service;

import com.likelion.seorang.dto.*;
import com.likelion.seorang.entity.Post;
import com.likelion.seorang.entity.PostLike;
import com.likelion.seorang.entity.User;
import com.likelion.seorang.enums.Role;
import com.likelion.seorang.exception.InvalidLikeException;
import com.likelion.seorang.exception.InvalidPostException;
import com.likelion.seorang.repository.PostLikeRepository;
import com.likelion.seorang.repository.PostRepository;
import com.likelion.seorang.repository.UsersRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UsersRepository usersRepository;
    private final PostLikeRepository postLikeRepository;
    private final S3Client s3Client;
    private final StringRedisTemplate redisTemplate;
    private final PostCacheService postCacheService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 피드 전체 조회
    @Cacheable(value = "posts", key = "'all'")
    @Transactional(readOnly = true)
    public List<PostListItemDto> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> {
                    long realLikeCount = postLikeRepository.countByPostId(post.getId());
                    post.updateLikeCount((int) realLikeCount);
                    return PostListItemDto.from(post);
                })
                .toList();
    }

    // 게시글 작성하기
    @Transactional
    public PostListItemDto createPost(Long userId, PostRequestDto postCreateDto){
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidPostException("유저가 없습니다.")
        );
        if(postCreateDto.getTag1() == null){
            throw new InvalidPostException("태그는 최소 1개 이상 작성해야 합니다.");
        }
        String imgUrl = upload(postCreateDto.getImgUrl());
        Post post = Post.builder()
                .user(user)
                .imgUrl(imgUrl)
                .tag1(postCreateDto.getTag1())
                .tag2(postCreateDto.getTag2())
                .tag3(postCreateDto.getTag3())
                .createdAt(LocalDateTime.now())
                .build();
        postRepository.save(post);
        postCacheService.evictPostsCache();
        return PostListItemDto.from(post);
    }

    // 게시글 수정하기
    @Transactional
    public PostListItemDto updatePost(Long userId, Long postId, PostRequestDto postUpdateDto){
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidPostException("유저가 없습니다.")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new InvalidPostException("수정하려는 게시글이 없습니다.")
        );

        if (!user.getId().equals(post.getUser().getId())){
            throw new InvalidPostException("해당 게시글을 작성한 유저만 수정할 수 있습니다.");
        }
        if(postUpdateDto.getTag1() == null){
            throw new InvalidPostException("태그는 최소 1개 이상 작성해야 합니다.");
        }

        // 새 이미지 들어온 경우
        String imgUrl = post.getImgUrl();
        if (postUpdateDto.getImgUrl() != null && !postUpdateDto.getImgUrl().isEmpty()) {
            String oldImg = imgUrl;
            String newImgUrl = upload(postUpdateDto.getImgUrl());
            try {
                delete(oldImg); // 버킷에 있는 기존 이미지 삭제
            } catch (Exception e) {
                log.error("S3 이미지 삭제 무시됨", e);
            }
            imgUrl = newImgUrl;
        }
        post.updatePost(imgUrl, postUpdateDto.getTag1(), postUpdateDto.getTag2(), postUpdateDto.getTag3());
        postRepository.save(post);

        postCacheService.evictPostsCache();
        return PostListItemDto.from(post);
    }

    // 게시글 삭제하기
    @Transactional
    public void deletePost(Long userId, Long postId) {
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidPostException("유저가 없습니다.")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new InvalidPostException("삭제하려는 게시글이 없습니다.")
        );

        if (!user.getId().equals(post.getUser().getId()) && !user.getRole().equals(Role.STAFF)){
            throw new InvalidPostException("해당 게시글을 작성한 유저만 삭제할 수 있습니다.");
        }
        try {
            delete(post.getImgUrl()); // 버킷에 있는 기존 이미지 삭제
        } catch (Exception e) {
            log.error("S3 이미지 삭제 무시됨", e);
        }
        postLikeRepository.deleteByPost_Id(postId);
        redisTemplate.delete("post:" + postId + ":likeCount");
        postRepository.delete(post);
        postCacheService.evictPostsCache();
    }


    /// * 헬퍼 메서드 * ///
    // s3 이미지 삽입
    public String upload(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucket).key(fileName))
                    .toExternalForm();

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // s3 이미지 삭제
    public void delete(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    @Scheduled(fixedRate = 300000) // 5분
    @Transactional
    public void syncLikeCount() {
        Set<String> dirtyKeys = redisTemplate.opsForSet().members("like:dirty");
        if (dirtyKeys == null || dirtyKeys.isEmpty()) return;

        for (String postIdStr : dirtyKeys) {
            try{
                Long postId = Long.parseLong(postIdStr);

                //DB count를 source of truth로 사용
                int actualCount = postLikeRepository.countByPost_Id(postId);

                postRepository.findById(postId)
                        .ifPresent(post -> post.updateLikeCount(actualCount));

                // 동기화 후 Redis 캐시 보정
                redisTemplate.opsForValue()
                        .set("post:" + postId + ":likeCount", String.valueOf(actualCount));
                redisTemplate.opsForSet().remove("like:dirty", postIdStr);
            }catch (Exception e){
                log.error("syncLikeCount 실패 postId={}", postIdStr, e);
            }
        }
    }
}
