package com.kakao.together.service.file.impl;

import com.kakao.together.controller.file.dto.RawMultipartFile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.exception.file.FileIOException;
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
    public RawMultipartFile processTempFileUpload(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (!fileValidator.isAllowedExtension(name)) {
            log.info("허용하지 않는 타입의 파일. filename: {}", file.getOriginalFilename());
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_FORMAT);
        }
        return uploadTempFile(file);
    }

    private RawMultipartFile uploadTempFile(MultipartFile file) {
        String extension = extractExtension(file.getOriginalFilename());
        String createdFileName = createRealFilename() + extension;

        Path storagePath = filePathResolver.resolveTempPath(createdFileName, file.getContentType());

        try {
            file.transferTo(storagePath);
        } catch (IOException e) {
            throw new FileIOException("임시 파일을 실제 저장경로로 옮기던 중 예외발생", e);
        }

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
                .orElseThrow(() -> {
                    log.info("파일형식이 존재하지 않는 파일. filename: {}", fileName);
                    return new CustomException(ErrorCode.UNSUPPORTED_FILE_FORMAT);
                });
    }

    @Override
    public void moveToStorageUpload(String url, String contentType) {

        String fileName = Paths.get(url).getFileName().toString();
        Path targetPath = filePathResolver.resolveUploadPath(fileName, contentType);
        try {
            Files.move(Path.of(url), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileIOException("파일 이동 도중 예외 발생", e);
        }
    }

    @Override
    public void deleteFile(String filename, String contentType) {

        Path targetPath = filePathResolver.resolveUploadPath(filename, contentType);
        boolean isDeleted = false;
        try {
            isDeleted = Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new FileIOException("파일 삭제도중 예외 발생", e);
        }

        if (!isDeleted) {
            log.error("존재해야 하는 파일이 존재하지 않아 정삭적으로 삭제완료 처리가 되지 않음; url: {}", filename);
        }
    }
}
