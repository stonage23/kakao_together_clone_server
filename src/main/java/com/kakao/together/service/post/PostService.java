package com.kakao.together.service.post;

import com.kakao.together.controller.dto.ContentDto;
import com.kakao.together.domain.entity.post.Post;
import lombok.NonNull;

import java.util.List;

public interface PostService {

    Post createPost(Post post);

    Post findPostById(Long postId);

    String postToHtml(@NonNull Post post);

    Long buildPost(Long postId, List<ContentDto.ContentCommand> contentCommands);
}
