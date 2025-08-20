package com.kakao.together.service.image;

import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.domain.entity.image.Image;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image createIfSrcNotExist(ImageDto dto) {
        return imageRepository.findByUrl(dto.getUrl()).orElseGet(
                () -> imageRepository.save(dto.toEntity())
        );
    }

    public Image getImageEntityById(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청하신 이미지를 찾을 수 없습니다. ")
        );
    }

    public Optional<Image> findImageById(Long imageId) {
        return imageRepository.findById(imageId);
    }

    public void delete(Image image) {
        imageRepository.delete(image);
    }
}
