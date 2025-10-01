package com.kakao.together.service.file;

import com.kakao.together.controller.file.dto.RawMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    RawMultipartFile processTempFileUpload(MultipartFile file) throws IOException;

    void moveToStorageUpload(String url, String contentType);

    void deleteFile(String realFileName, String contentType);
}
