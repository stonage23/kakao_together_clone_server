package com.kakao.together.file.service.impl;

import com.kakao.together.file.controller.dto.RawMultipartFile;
import com.kakao.together.file.controller.dto.FileDto.FileResponse;
import com.kakao.together.file.domain.FileStatus;
import com.kakao.together.file.domain.FileInfo;
import com.kakao.together.file.repository.FileInfoRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.file.resolver.ResourceUrlResolver;
import com.kakao.together.file.service.FileTempUploadService;
import com.kakao.together.file.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileTempUploadServiceImpl implements FileTempUploadService {

    private final FileInfoRepository fileInfoRepository;
    private final FileStorageService fileStorageService;
    private final ResourceUrlResolver resourceUrlResolver;

    /**
     * 파일 임시 업로드시 FileStatus.PENDING 상태로 저장된다. 만약 해당 파일이 실제 업로드 상태가 된다면 FileStatus.USED 상태로 업데이트해야한다.
     * @param file
     * @return
     */
    @Override
    public FileResponse processTempUpload(MultipartFile file) {
        RawMultipartFile rawMultipartFile;
        try {
            rawMultipartFile = fileStorageService.processTempFileUpload(file);
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
                .url(resourceUrlResolver.resolveTempUrl(createdFileInfo.getSavedName(), createdFileInfo.getContentType()))
                .size(fileInfo.getSize())
                .contentType(fileInfo.getContentType())
                .build();
    }
}
