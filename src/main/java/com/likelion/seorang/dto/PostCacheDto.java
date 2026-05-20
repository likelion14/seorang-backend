package com.likelion.seorang.dto;

import com.likelion.seorang.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostCacheDto {

    private Long postId;
    private String imgUrl;
    private String tag1;
    private String tag2;
    private String tag3;
    private Long authorId;
    private Integer likeCount;

    public static PostCacheDto from(Post post) {
        return PostCacheDto.builder()
                .postId(post.getId())
                .imgUrl(post.getImgUrl())
                .tag1(post.getTag1())
                .tag2(post.getTag2())
                .tag3(post.getTag3())
                .authorId(post.getUser().getId())
                .likeCount(post.getLikeCount())
                .build();
    }
}