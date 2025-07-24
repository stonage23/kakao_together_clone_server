package com.kakao.together.service.file;

import com.kakao.together.controller.dto.FileDto;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
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

@Service
@Slf4j
public class FileServiceLocal implements FileService<FileDto> {

    private final static String UPLOAD_DIR = "src/main/resources/files/content";
    private final static String TEMP_DIR = "src/main/resources/files/temporary";

    @PostConstruct
    public void init() {
        FileManageUtil.createDirIfNotExists(UPLOAD_DIR);
        FileManageUtil.createDirIfNotExists(TEMP_DIR);
    }

    @Override
    public FileDto saveFile(MultipartFile file, @Nullable String targetUrl) {
        if (targetUrl == null) {
            targetUrl = TEMP_DIR;
        }
        final String finalTargetUrl = targetUrl;
        String originalFileName = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VALUE, "업로드하려는 파일의 originalFileName 값이 필요합니다."));

        Path createdUrl = Path.of(finalTargetUrl).resolve(originalFileName).toAbsolutePath();
        try {
            file.transferTo(createdUrl);
        } catch (IOException e) {
            throw new CustomException("파일 임시저장 실패: " + file.getOriginalFilename(), e);
        }
        return FileDto.builder()
                .filePath(targetUrl.toString())
                .fileName(originalFileName)
                .build();
    }

    @Override
    public void moveFile(List<FileDto> fileList, @Nullable String targetUrl) {
        if (targetUrl == null || targetUrl.isEmpty()) {
            targetUrl = UPLOAD_DIR;
        }
        final String finalTargetUrl = targetUrl;
        fileList.forEach(file -> {
                    try {
                        String savedPath = Optional.ofNullable(file.getFilePath()).orElseThrow(() -> new CustomException("FileDto의 filePath필드가 null"));
                        Files.move(Path.of(savedPath), Path.of(finalTargetUrl), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new CustomException("임시저장 파일 실제 저장 디렉토리로 이동 실패: " + file.getFilePath(), e);
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
