package com.kakao.together.service.content;

import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public List<ImageContent> getImageContentsByPost(Long postId) {
        return contentRepository.findAllByTypeAndPostId(postId);
    }

    public void deleteContent(Long contentId) {
        contentRepository.findById(contentId).ifPresent(contentRepository::delete);
    }

    public ImageContent findImageContentByImageId(Long imageId) {
        return contentRepository.findByImageId(imageId);
    }
}
