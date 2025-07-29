package com.kakao.together.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService<T> {

    T saveFile(MultipartFile file);

    void moveFile(List<T> fileList);

    void deleteFile(String url);
}
