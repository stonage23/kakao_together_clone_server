package com.kakao.together.service.post;

import com.kakao.together.controller.post.dto.ContentDto.ContentCommand;
import com.kakao.together.domain.entity.post.Post;

import java.util.List;

import static com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;

public interface PostService {

    Post createPost(Post post);

    Post findPostById(Long postId);

    Long buildPost(EditFundraisingRequest request);

    void beforeUpdatePost(String html, Long postId);

    List<ContentCommand> extractContentsFromHtml(String html);
}
