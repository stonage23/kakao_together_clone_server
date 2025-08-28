package com.kakao.together.service.post;

import com.kakao.together.controller.dto.ContentDto.ContentCommand;
import com.kakao.together.domain.entity.post.Post;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;

public interface PostService {

    Post createPost(Post post);

    Post findPostById(Long postId);

    String postToHtml(@NonNull Post post);

    @Transactional
    Long buildPost(EditFundraisingRequest request);

    void beforeUpdatePost(String html, Long postId);

    @Transactional
    List<ContentCommand> extractContentsFromHtml(String html);
}
