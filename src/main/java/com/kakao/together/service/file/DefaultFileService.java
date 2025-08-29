package com.kakao.together.service.file;

import com.kakao.together.api.filestorage.RawMultipartFile;
import com.kakao.together.controller.file.dto.FileDto.FileResponse;
import com.kakao.together.domain.entity.file.FileStatus;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.repository.FileInfoRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.file.impl.FilePathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DefaultFileService implements FileService {

    private final FileInfoRepository fileInfoRepository;
    private final FileStorageService fileStorageService;
    private final FilePathResolver filePathResolver;

    @Override
    public FileResponse processTempUpload(MultipartFile file) {
        RawMultipartFile rawMultipartFile;
        try {
            rawMultipartFile = fileStorageService.processUpload(file);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILED_UPLOAD_FILE);
        }

        FileInfo fileInfo = FileInfo.builder()
                .originalName(rawMultipartFile.getOriginalFilename())
                .savedName(rawMultipartFile.getSavedFileName())
                .contentType(rawMultipartFile.getContentType())
                .extension(rawMultipartFile.getExtension())
                .size(rawMultipartFile.getSize())
                .status(FileStatus.PENDING)
                .build();

        FileInfo createdFileInfo = fileInfoRepository.save(fileInfo);

        return FileResponse.builder()
                .id(fileInfo.getId())
                .originalName(fileInfo.getOriginalName())
                .url(filePathResolver.resolveTempPath(createdFileInfo.generateFilename(), createdFileInfo.getContentType()).toString())
                .size(fileInfo.getSize())
                .contentType(fileInfo.getContentType())
                .build();
    }
}
