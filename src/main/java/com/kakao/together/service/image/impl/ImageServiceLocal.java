package com.kakao.together.service.image.impl;

import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.service.image.FileService;
import com.kakao.together.util.FileManageUtil;
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

    private static final String UPLOAD_DIR = "src/main/resources/content";
    private static final String TEMP_DIR = "src/main/resources/temporary";

    @PostConstruct
    void init() {
        FileManageUtil.createDirIfNotExists(UPLOAD_DIR);
        FileManageUtil.createDirIfNotExists(TEMP_DIR);
    }

    @Override
    public ImageDto saveTempImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = Optional.ofNullable(originalFilename)
                    .filter(ofn -> ofn.contains("."))
                    .map(ofn -> ofn.substring(originalFilename.lastIndexOf(".")))
                    .orElseGet(() -> "");
            String createdFileName = UUID.randomUUID() + extension;

            Path storagePath = Paths.get(TEMP_DIR).resolve(createdFileName).toAbsolutePath();

            file.transferTo(storagePath);

            return ImageDto.builder()
                    .originalName(originalFilename)
                    .savedPath(storagePath.toString())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("이미지 임시저장 실패", e);
        }
    }

    @Override
    public void updateTempToReal(List<ImageDto> imageList, String to) {
        imageList.forEach(image -> {
                    String fileName = Paths.get(image.getSavedPath()).getFileName().toString();
                    Path targetPath = Paths.get(to).resolve(fileName);
                    try {
                        Files.move(Path.of(image.getSavedPath()), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException("임시 파일 실제 저장 디렉토리로 이동 실패: " + image.getSavedPath(), e);
                    }
                }
        );
    }

    @Override
    public void deleteFile(String savedUrl) {
        if (savedUrl == null) {
            log.error("파일 저장 경로 url이 null");
            throw new NullPointerException("파일 저장 경로 url 이 null");
        }
        try {
            String fileName = Paths.get(savedUrl).getFileName().toString();
            Path path = Paths.get(TEMP_DIR).resolve(fileName);

            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패" + savedUrl, e);
        } catch (InvalidPathException e) {
            throw new RuntimeException("파일 저장 경로 url 형식이 맞지 않음: " + savedUrl, e);
        }
    }
}
