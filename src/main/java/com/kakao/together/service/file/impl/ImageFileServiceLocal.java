package com.kakao.together.service.file.impl;

import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageFileServiceLocal implements FileService<ImageDto> {

    private final ImagePathResolver imagePathResolver;

    @Override
    public ImageDto saveFile(MultipartFile file) {
        String originalFileName = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VALUE, "업로드하려는 파일의 originalFileName 값이 필요합니다."));
        String extension = Optional.ofNullable(originalFileName)
                .filter(ofn -> ofn.contains("."))
                .map(ofn -> ofn.substring(originalFileName.lastIndexOf(".")))
                .filter(ofn -> ofn.length() != 0)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_FORMAT));
        String createdFileName = UUID.randomUUID() + extension;

        Path storagePath = imagePathResolver.defaultBuildTempPath(createdFileName);

        try {
            file.transferTo(storagePath);
        } catch (IOException e) {
            throw new CustomException("이미지 임시저장 실패", e);
        }
            return ImageDto.builder()
                    .originalName(originalFileName)
                    .realName(createdFileName)
                    .url(storagePath.toString())
                    .build();
    }

    @Override
    public void moveFile(List<ImageDto> fileList) {

        fileList.forEach(imageDto -> {
            Path targetPath = imagePathResolver.defaultBuildUploadPath(imageDto.getRealName());
                    try {
                        Files.move(Path.of(imageDto.getUrl()), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new CustomException("임시 파일 실제 저장 디렉토리로 이동 실패: " + imageDto.getRealName(), e);
                    }
                }
        );
    }

    @Override
    public void deleteFile(String url) {
        if (url == null) {
            log.error("파일 저장 경로 url이 null");
            throw new CustomException("파일 저장 경로 url 이 null");
        }
        try {
            Files.deleteIfExists(Paths.get(url));
        } catch (IOException e) {
            throw new CustomException("파일 삭제 실패" + url, e);
        } catch (InvalidPathException e) {
            throw new CustomException("파일 저장 경로 url 형식이 맞지 않음: " + url, e);
        }
    }
}
