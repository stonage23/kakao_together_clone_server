package com.kakao.together.service.image;

import com.kakao.together.controller.image.dto.ImageCommand;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.domain.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileInfoRepository fileInfoRepository;

    public FileInfo createIfSrcNotExist(ImageCommand dto) {
        return fileInfoRepository.findByUrl(dto.getUrl()).orElseGet(
                () -> fileInfoRepository.save(dto.toEntity())
        );
    }

    public FileInfo getImageEntityById(Long imageId) {
        return fileInfoRepository.findById(imageId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청하신 이미지를 찾을 수 없습니다. ")
        );
    }

    public Optional<FileInfo> findImageById(Long imageId) {
        return fileInfoRepository.findById(imageId);
    }

    public void delete(FileInfo fileInfo) {
        fileInfoRepository.delete(fileInfo);
    }
}
