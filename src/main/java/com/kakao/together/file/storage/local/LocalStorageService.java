package com.kakao.together.file.storage.local;

import com.kakao.together.file.controller.dto.RawMultipartFile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.exception.file.FileIOException;
import com.kakao.together.file.storage.FileStorageService;
import com.kakao.together.file.helper.FileValidator;
import com.kakao.together.file.helper.LogicalPathMapper;
import com.kakao.together.util.FileManageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local-storage")
public class LocalStorageService implements FileStorageService {

    private final FileValidator fileValidator;
    private final LogicalPathMapper logicalPathMapper;

    @Value("${storage.local.root_dir}")
    private String ROOT_DIR;

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
        String extension = FileManageUtil.extractExtension(file.getOriginalFilename());
        String createdFileName = createRealFilename() + extension;


        Path storagePath = Path.of(ROOT_DIR).resolve(logicalPathMapper.getTempPrefix(file.getContentType())).resolve(createdFileName);

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
                .build();
    }

    private String createRealFilename() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void moveToStorageUpload(String filename, String contentType) {

        Path fromPath = Path.of(ROOT_DIR).resolve(logicalPathMapper.getTempPrefix(contentType)).resolve(filename);
        Path targetPath = Path.of(ROOT_DIR).resolve(logicalPathMapper.getUploadPrefix(contentType)).resolve(filename);
        try {
            Files.move(fromPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileIOException("파일 이동 도중 예외 발생", e);
        }
    }

    @Override
    public void deleteFile(String filename, String contentType) {

        Path targetPath = Path.of(ROOT_DIR).resolve(logicalPathMapper.getUploadPrefix(contentType)).resolve(filename);

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
