package com.likelion.seorang.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
// Redis 기반으로 리프레시 토큰의 저장/조회/삭제 기능을 제공하는 클래스
// 예전 플젝 코드 가져온거라 매개변수가 email로 되어있는데 수정해서 사용하면 됨~~
public class RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate; // Redis 클라이언트

    @Value("${app.jwt.refresh-exp-day}")
    private long refreshExpDay;

    // 리프레시 토큰 저장
    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue().set(
                key(email), refreshToken,
                Duration.ofDays(refreshExpDay)
        );
    }

    // 리프레시 토큰 조회
    public String find(String email) {
        return redisTemplate.opsForValue().get(key(email));
    }

    // 리프레시 토큰 삭제 (로그아웃/재발급 시 사용)
    public void delete(String email) {
        redisTemplate.delete(key(email));
    }


    // 내부 키 생성 규칙
    private String key(String email) {
        return "refresh: " + email;
    }
}
