package com.kakao.together.service;

import com.kakao.together.controller.dto.ContentDto.ImageContentDto;
import com.kakao.together.controller.dto.ContentDto.TextContentDto;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kakao.together.controller.dto.ContentDto.SubtitleContentDto;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public void createSubtitleContent(SubtitleContentDto subtitleContentDto) {
        contentRepository.save(subtitleContentDto.toEntity());
    }

    public void createTextContent(TextContentDto textContentDto) {
        contentRepository.save(textContentDto.toEntity());
    }

    public void createImageContent(ImageContentDto imageContentDto) {
        contentRepository.save(imageContentDto.toEntity());
    }

    public List<ImageContent> getImageContentsByPost(Post post) {
        return contentRepository.findAllByTypeAndPostId(post.getId());
    }

    public void deleteContent(Long contentId) {
        contentRepository.findById(contentId).ifPresent(contentRepository::delete);
    }

    public ImageContent findImageContentByImageId(Long imageId) {
        return contentRepository.findByImageId(imageId);
    }
}
