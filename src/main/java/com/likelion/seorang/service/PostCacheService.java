package com.likelion.seorang.service;

import com.likelion.seorang.dto.PostCacheDto;
import com.likelion.seorang.repository.PostRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final PostRepository postRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY = "posts:all";

    public List<PostCacheDto> getCachedPosts() {

        String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY);

        // 캐시 존재
        if (cachedJson != null) {
            try {
                return objectMapper.readValue(
                        cachedJson,
                        new TypeReference<List<PostCacheDto>>() {}
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // DB 조회
        List<PostCacheDto> posts =
                postRepository.findAllByOrderByCreatedAtDesc()
                        .stream()
                        .map(PostCacheDto::from)
                        .toList();

        // JSON으로 캐시 저장
        try {
            redisTemplate.opsForValue().set(
                    CACHE_KEY,
                    objectMapper.writeValueAsString(posts),
                    10,
                    TimeUnit.MINUTES
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return posts;
    }

    public void evictPostsCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}