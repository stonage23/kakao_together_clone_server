package com.kakao.together.service;

import com.kakao.together.domain.entity.Image;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image createIfSrcNotExist(String src) {
        return new Image();
    }

    public Image getImageEntityById(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청하신 이미지를 찾을 수 없습니다. ")
        );
    }
}
