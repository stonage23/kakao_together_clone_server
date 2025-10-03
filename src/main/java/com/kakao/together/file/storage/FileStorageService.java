package com.kakao.together.file.storage;

import com.kakao.together.file.controller.dto.RawMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    RawMultipartFile processTempFileUpload(MultipartFile file) throws IOException;

    void moveToStorageUpload(String filename, String contentType);

    void deleteFile(String filename, String contentType);
}
