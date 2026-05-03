package com.likelion.seorang.service;

import com.likelion.seorang.common.GlobalExceptionHandler;
import com.likelion.seorang.dto.LikeResponse;
import com.likelion.seorang.dto.PostCreateDto;
import com.likelion.seorang.dto.PostListItemDto;
import com.likelion.seorang.dto.PostUpdateDto;
import com.likelion.seorang.entity.Post;
import com.likelion.seorang.entity.PostLike;
import com.likelion.seorang.entity.User;
import com.likelion.seorang.enums.Role;
import com.likelion.seorang.exception.InvalidLikeException;
import com.likelion.seorang.exception.InvalidPostException;
import com.likelion.seorang.repository.PostLikeRepository;
import com.likelion.seorang.repository.PostRepository;
import com.likelion.seorang.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
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
    private final S3Client s3Client; // 🔥 주입

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 피드 전체 조회
    public List<PostListItemDto> findAll() {
        return postRepository.findAll().stream()
                .map(PostListItemDto::from)
                .toList();
    }

    // 게시글 좋아요
    @Transactional
    public LikeResponse likePost(Long userId, Long postId) {
        Post likedPost = postRepository.findById(postId).orElseThrow(
                () -> new InvalidLikeException("좋아요를 누를 게시글이 없습니다."));

        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidLikeException("유저가 없습니다.")
        );

        // 이미 좋아요 누른 게시글
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            return LikeResponse.of(false, likedPost.decreaseLikeCount());
        }

        // 새로운 좋아요 생성
        PostLike postLike = PostLike.builder()
                .post(likedPost)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        postLikeRepository.save(postLike);

        return LikeResponse.of(true, likedPost.increaseLikeCount());
    }

    // 게시글 작성하기
    @Transactional
    public PostListItemDto createPost(Long userId, PostCreateDto postCreateDto){
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidPostException("유저가 없습니다.")
        );
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
        return PostListItemDto.from(post);
    }

    // 게시글 수정하기
    @Transactional
    public PostListItemDto updatePost(Long userId, PostUpdateDto postUpdateDto){
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidPostException("유저가 없습니다.")
        );
        Post post = postRepository.findById(postUpdateDto.getPostId()).orElseThrow(
                () -> new InvalidPostException("수정하려는 게시글이 없습니다.")
        );

        if (!user.getId().equals(post.getUser().getId())){
            throw new InvalidPostException("해당 게시글을 작성한 유저만 수정할 수 있습니다.");
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

        return PostListItemDto.from(post);
    }

    // 게시글 삭제하기
    @Transactional
    public void deletePost(Long userId, Long postId) {
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new InvalidPostException("유저가 없습니다.")
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new InvalidPostException("수정하려는 게시글이 없습니다.")
        );

        if (!user.getId().equals(post.getUser().getId()) && !user.getRole().equals(Role.STAFF)){
            throw new InvalidPostException("해당 게시글을 작성한 유저만 삭제할 수 있습니다.");
        }
        try {
            delete(post.getImgUrl()); // 버킷에 있는 기존 이미지 삭제
        } catch (Exception e) {
            log.error("S3 이미지 삭제 무시됨", e);
        }
        postRepository.delete(post);
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
}
