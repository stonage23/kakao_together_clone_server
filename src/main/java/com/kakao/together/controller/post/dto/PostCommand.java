package com.kakao.together.controller.post.dto;

import com.kakao.together.controller.post.dto.ContentDto.ContentCommand;
import com.kakao.together.domain.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class PostCommand {

    private Long postId;
    private List<ContentCommand> contents;

    public Post toEntity() {
        return Post.builder()
                .build();
    }
}
