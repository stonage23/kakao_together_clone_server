package com.kakao.together.service.post;

import com.kakao.together.domain.entity.post.Post;

public interface PostService {

    Post createPost(Post post);

    Post findPostById(Long postId);
}
