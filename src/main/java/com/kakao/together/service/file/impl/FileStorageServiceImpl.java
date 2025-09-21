package com.kakao.together.service.file.impl;

import com.kakao.together.controller.file.dto.RawMultipartFile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileValidator fileValidator;
    private final FilePathResolver filePathResolver;

    /**
     * 확장자 검증을 거치고 파일 업로드
     * @param file
     * @return
     */
    @Override
    public RawMultipartFile processUpload(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (!fileValidator.isAllowedExtension(name)) {
            throw new CustomException(ErrorCode.NOT_VALID_FORMAT);
        }
        return uploadFile(file);
    }

    private RawMultipartFile uploadFile(MultipartFile file) throws IOException {
        String extension = extractExtension(file.getOriginalFilename());
        String createdFileName = createRealFilename() + extension;

        Path storagePath = filePathResolver.resolveTempPath(createdFileName, file.getContentType());

        file.transferTo(storagePath);

        return RawMultipartFile.builder()
                .originalFilename(file.getOriginalFilename())
                .savedFileName(createdFileName)
                .extension(extension)
                .contentType(file.getContentType())
                .size(file.getSize())
                .url(storagePath.toString())
                .build();
    }

    private String createRealFilename() {
        return UUID.randomUUID().toString();
    }

    private String extractExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(ofn -> ofn.contains("."))
                .map(ofn -> ofn.substring(fileName.lastIndexOf(".")))
                .filter(ofn -> ofn.length() != 0)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_FORMAT));
    }

    @Override
    public void moveFile(String url, String contentType) throws IOException {

        String fileName = Paths.get(url).getFileName().toString();
        Path targetPath = filePathResolver.resolveUploadPath(fileName, contentType);
        Files.move(Path.of(url), targetPath, StandardCopyOption.REPLACE_EXISTING);
        throw new CustomException("임시 파일 실제 저장 디렉토리로 이동 실패: " + url);
    }

    @Override
    public void deleteFile(String filename, String contentType) throws IOException {

        Path targetPath = filePathResolver.resolveUploadPath(filename, contentType);
        boolean isDeleted = Files.deleteIfExists(targetPath);

        if (!isDeleted) {
            log.error("존재해야 하는 파일이 존재하지 않아 정삭적으로 삭제완료 처리가 되지 않음; url: {}", filename);
        }
    }
}
