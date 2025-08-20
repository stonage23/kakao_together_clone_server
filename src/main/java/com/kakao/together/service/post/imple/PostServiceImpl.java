package com.kakao.together.service.post.imple;

import com.kakao.together.controller.dto.ContentDto.ContentCommand;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.domain.entity.post.PostType;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.repository.ContentRepository;
import com.kakao.together.domain.repository.PostRepository;
import com.kakao.together.service.post.PostService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ContentRepository contentRepository;

    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 id에 해당하는 POST 엔티티가 존재하지 않습니다."));
    }

    @Override
    public String postToHtml(@NonNull Post post) {
        StringBuilder html = new StringBuilder();
        for (Content content : post.getContents()) {
            if (content instanceof SubTitleContent subTitleContent) {
                html.append(subTitleContent.getSubtitle());
            } else if (content instanceof TextContent textContent) {
                html.append(textContent.getText());
            } else if (content instanceof ImageContent imageContent) {
                html.append(imageContent.getImage());
            } else throw new CustomException(ErrorCode.NOT_VALID_TAG, "허용 외 타입의 콘텐츠가 DB에 저장되어 있어 HTML 구성 실패");
        }
        return html.toString();
    }

    @Override
    @Transactional
    public Long buildPost(Long postId, List<ContentCommand> contentCommands) {
        Post createdPost = null;

        if (postId == null) {
            Post post = Post.builder()
                    .type(PostType.STORY)
                    .build();
            createdPost = postRepository.save(post);
        } else {
            log.error("##### 존재해야 하는 Post 엔티티가 존재하지 않음: postId: {}; 발생 위치: {}", postId, "PostServiceImpl.buildPost");
            createdPost = postRepository.findById(postId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; postId: " + postId));
        }

        final Post finalCreatedPost = createdPost;

        contentCommands.forEach(contentCommand ->
                contentRepository.save(contentCommand.toEntity(finalCreatedPost)));

        return finalCreatedPost.getId();
    }
}
