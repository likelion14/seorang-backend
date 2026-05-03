package com.likelion.seorang.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {
    Integer likeCount;
    Boolean liked;

    public static LikeResponse of(boolean liked, int likeCount) {
        return LikeResponse.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }
}
