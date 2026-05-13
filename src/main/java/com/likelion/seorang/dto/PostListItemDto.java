package com.likelion.seorang.dto;

import com.likelion.seorang.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@Builder
public class PostListItemDto {
    private Long postId;
    private String imgUrl;
    private String tag1;
    private String tag2;
    private String tag3;
    private Integer likeCount;
    private Long authorId;

    public static PostListItemDto from(Post post) {
        return PostListItemDto.builder()
                .postId(post.getId())
                .imgUrl(post.getImgUrl())
                .tag1(post.getTag1())
                .tag2(post.getTag2())
                .tag3(post.getTag3())
                .likeCount(post.getLikeCount())
                .authorId(post.getUser().getId())
                .build();
    }
}
