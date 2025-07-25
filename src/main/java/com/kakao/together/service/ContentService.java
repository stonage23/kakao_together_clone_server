package com.kakao.together.service;

import com.kakao.together.controller.dto.ContentDto;
import com.kakao.together.controller.dto.ContentDto.ImageContentDto;
import com.kakao.together.controller.dto.ContentDto.TextContentDto;
import com.kakao.together.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public void createSubtitleContent(ContentDto.SubtitleContentDto subtitleContentDto) {
        contentRepository.save(subtitleContentDto.toEntity());
    }

    public void createTextContent(TextContentDto textContentDto) {
        contentRepository.save(textContentDto.toEntity());
    }

    public void createImageContent(ImageContentDto imageContentDto) {
          contentRepository.save(imageContentDto.toEntity());
    }
}
