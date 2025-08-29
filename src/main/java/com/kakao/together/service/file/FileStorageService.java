package com.kakao.together.service.file;

import com.kakao.together.api.filestorage.RawMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    RawMultipartFile processUpload(MultipartFile file) throws IOException;

    void moveFile(String url, String contentType) throws IOException;

    void deleteFile(String realFileName, String contentType) throws IOException;
}
