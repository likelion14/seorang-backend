# Redis 사용 가이드

> 크게 두 가지 방식으로 Redis를 사용함
>
> 1. `@Cacheable` / `@CacheEvict` - 자동 캐싱 (부스 데이터, 좋아요 등)
> 2. `StringRedisTemplate` - 직접 조작 (Refresh Token)

---

## 1. @Cacheable - 자동 캐싱

메서드 반환값을 Redis에 자동으로 저장함
**같은 조건으로 다시 호출되면 DB 조회 없이 Redis에서 바로 반환함**

### 문법

```java
@Cacheable(value = "캐시이름", key = "키표현식")
```

| 속성 | 설명 | 예시 |
| --- | --- | --- |
| `value` | 캐시 이름 (TTL은 RedisConfig에서 설정) | `"booth"`, `"likes"` |
| `key` | Redis에 저장될 키 (생략 시 파라미터 자동 사용) | `"#boothId"`, `"#root.method.name"` |
| `condition` | 특정 조건일 때만 캐싱 | `"#id > 0"` |

### 예시

```java
// 부스 전체 목록 캐싱 - key 생략 시 메서드명 자동 사용
@Cacheable("booth")
public List<BoothResponse> getBooths() {
    return boothRepository.findAll()
            .stream().map(BoothResponse::from).toList();
}

// 특정 부스 단건 캐싱 - boothId를 key로 사용
@Cacheable(value = "booth", key = "#boothId")
public BoothResponse getBooth(Long boothId) {
    return BoothResponse.from(boothRepository.findById(boothId).orElseThrow());
}
```

### 동작 흐름

```
getBooths() 호출
    ├── Redis에 "booth" 키 존재? → Redis에서 바로 반환 ✅
    └── 없으면 → DB 조회 → 결과를 Redis에 저장 → 반환
```

---

## 2. @CacheEvict - 캐시 삭제

데이터가 변경될 때 기존 캐시를 삭제함
**삭제 후 다음 조회 시 DB에서 최신 데이터를 다시 가져와 캐싱함**

### 문법

```java
@CacheEvict(value = "캐시이름", key = "키표현식", allEntries = true/false)
```

| 속성 | 설명 |
| --- | --- |
| `value` | 삭제할 캐시 이름 |
| `key` | 삭제할 특정 키 (특정 항목만 삭제할 때) |
| `allEntries` | `true`면 해당 캐시 전체 삭제 |

### 예시

```java
// 부스 정보 수정 시 → 전체 부스 캐시 삭제
@CacheEvict(value = "booth", allEntries = true)
public void updateBooth(Long boothId, BoothUpdateRequest request) {
    Booth booth = boothRepository.findById(boothId).orElseThrow();
    booth.update(request);
}

// 특정 부스만 캐시 삭제
@CacheEvict(value = "booth", key = "#boothId")
public void deleteBooth(Long boothId) {
    boothRepository.deleteById(boothId);
}
```

### 주의사항

- 데이터를 **수정/삭제하는 메서드**에는 반드시 `@CacheEvict`를 붙여야 함
- 붙이지 않으면 DB는 바뀌었는데 Redis엔 옛날 데이터가 남아 **데이터 불일치 발생**

---

## 3. 현재 등록된 캐시 목록

`RedisConfig.java`에 캐시 이름과 TTL이 등록되어 있음

| 캐시 이름 | TTL | 용도 |
| --- | --- | --- |
| `"booth"` | 3시간 | 학과 부스 데이터 |
| `"likes"` | 2분 | 피드 좋아요 수 |
| 그 외 | 30분 | 기본값 |

새로운 캐시가 필요하면 `RedisConfig.java`의 `cacheManager` 빈에 추가하면 됨

```java
cacheConfigs.put("새 캐시 이름", defaultConfig().entryTtl(Duration.ofMinutes(10)));
```

---

## 4. RefreshTokenRepository - 직접 조작 방식

Refresh Token은 캐시가 아니라 **직접 저장/조회/삭제**가 필요해서 `StringRedisTemplate`을 사용함
`@Cacheable`과 달리 어노테이션 없이 코드로 직접 Redis를 제어하는 방식

```java
// 저장 (로그인 시)
refreshTokenRepository.save(email, refreshToken);

// 조회 (토큰 재발급 시)
String token = refreshTokenRepository.find(email);

// 삭제 (로그아웃 시)
refreshTokenRepository.delete(email);
```

### Redis에 저장되는 구조

```
key   : "refresh:user@email.com"
value : "eyJhbGciOiJIUzI1NiJ9..."
TTL   : 14일 (application.properties의 app.jwt.refresh-exp-day 값)
```