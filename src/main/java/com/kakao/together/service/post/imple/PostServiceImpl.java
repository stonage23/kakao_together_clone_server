package com.kakao.together.service.post.imple;

import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.repository.PostRepository;
import com.kakao.together.service.post.PostService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

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
}
