package com.kakao.together.service.file.impl;

import com.kakao.together.controller.file.dto.FileDto;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceLocal implements FileService<FileDto> {

    private final FilePathResolver filePathResolver;

    @Override
    public FileDto saveFile(MultipartFile file) {
        String originalFileName = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VALUE, "업로드하려는 파일의 originalFileName 값이 필요합니다."));

        String extension = Optional.ofNullable(originalFileName)
                .filter(ofn -> ofn.contains("."))
                .map(ofn -> ofn.substring(originalFileName.lastIndexOf(".")))
                .filter(ofn -> ofn.length() != 0)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_FORMAT));

        String createdFileName = UUID.randomUUID() + extension;

        Path createdUrl = filePathResolver.defaultBuildTempPath(createdFileName);
        try {
            file.transferTo(createdUrl);
        } catch (IOException e) {
            throw new CustomException("파일 임시저장 실패: " + file.getOriginalFilename(), e);
        }
        return FileDto.builder()
                .url(createdUrl.toString())
                .realName(createdFileName)
                .originalName(originalFileName)
                .build();
    }

    @Override
    public void moveFile(List<FileDto> fileList) {
        fileList.forEach(fileDto -> {
                    try {
                        String savedPath = Optional.ofNullable(fileDto.getRealName()).orElseThrow(() -> new CustomException("FileDto의 filePath필드가 null"));
                        Path targetUrl = filePathResolver.defaultBuildUploadPath(fileDto.getRealName());
                        Files.move(Path.of(savedPath), targetUrl, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new CustomException("임시저장 파일 실제 저장 디렉토리로 이동 실패: " + fileDto.getRealName(), e);
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
            Files.deleteIfExists(Path.of(url));
        } catch (IOException e) {
            throw new CustomException("파일 삭제 실패; 파일 저장 url: " + url, e);
        } catch (InvalidPathException e) {
            throw new CustomException("파일 저장 경로 url 형식이 맞지 않음; url: " + url, e);
        }
    }
}
