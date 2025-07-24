package com.kakao.together.service.file;

import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService<T> {

    T saveFile(MultipartFile file, @Nullable String targetUrl);

    void moveFile(List<T> fileList, @Nullable String targetUrl);

    void deleteFile(String url);
}
