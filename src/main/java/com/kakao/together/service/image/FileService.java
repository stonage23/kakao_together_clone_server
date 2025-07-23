package com.kakao.together.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService<T> {
    T saveTempImage(MultipartFile file);

    void updateTempToReal(List<T> imageList, String targetUrl);

    void deleteFile(String url);
}
