package com.kakao.together.service.file.impl;

import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.exception.CustomException;
import com.kakao.together.service.file.FileService;
import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ImageServiceLocal implements FileService<ImageDto> {

    private static final String UPLOAD_DIR = "src/main/resources/imgs/content";
    private static final String TEMP_DIR = "src/main/resources/imgs/temporary";

    @PostConstruct
    void init() {
        FileManageUtil.createDirIfNotExists(UPLOAD_DIR);
        FileManageUtil.createDirIfNotExists(TEMP_DIR);
    }

    @Override
    public ImageDto saveFile(MultipartFile file, @Nullable String targetUrl) {
        if (targetUrl == null) {
            targetUrl = TEMP_DIR;
        }
        final String finalTargetUrl = targetUrl;
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = Optional.ofNullable(originalFilename)
                    .filter(ofn -> ofn.contains("."))
                    .map(ofn -> ofn.substring(originalFilename.lastIndexOf(".")))
                    .orElseGet(() -> "");
            String createdFileName = UUID.randomUUID() + extension;

            Path storagePath = Paths.get(finalTargetUrl).resolve(createdFileName).toAbsolutePath();

            file.transferTo(storagePath);

            return ImageDto.builder()
                    .originalName(originalFilename)
                    .savedPath(storagePath.toString())
                    .build();

        } catch (IOException e) {
            throw new CustomException("이미지 임시저장 실패", e);
        }
    }

    @Override
    public void moveFile(List<ImageDto> fileList, @Nullable String to) {
        if (to == null || to.isEmpty()) {
            to = UPLOAD_DIR;
        }
        String finalTo = to;
        fileList.forEach(image -> {
                    String fileName = Paths.get(image.getSavedPath()).getFileName().toString();
                    Path targetPath = Paths.get(finalTo).resolve(fileName);
                    try {
                        Files.move(Path.of(image.getSavedPath()), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new CustomException("임시 파일 실제 저장 디렉토리로 이동 실패: " + image.getSavedPath(), e);
                    }
                }
        );
    }

    @Override
    public void deleteFile(String savedUrl) {
        if (savedUrl == null) {
            log.error("파일 저장 경로 url이 null");
            throw new CustomException("파일 저장 경로 url 이 null");
        }
        try {
            Files.deleteIfExists(Paths.get(savedUrl));
        } catch (IOException e) {
            throw new CustomException("파일 삭제 실패" + savedUrl, e);
        } catch (InvalidPathException e) {
            throw new CustomException("파일 저장 경로 url 형식이 맞지 않음: " + savedUrl, e);
        }
    }
}
