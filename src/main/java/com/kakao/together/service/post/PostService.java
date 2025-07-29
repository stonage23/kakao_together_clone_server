package com.kakao.together.service.post;

import com.kakao.together.domain.entity.post.Post;
import lombok.NonNull;

public interface PostService {

    Post createPost(Post post);

    Post findPostById(Long postId);

    String postToHtml(@NonNull Post post);
}
