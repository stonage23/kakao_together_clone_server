package com.kakao.together.service.file.impl;

import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FilePathResolver {

    private final static String UPLOAD_DIR = "src/main/resources/files/content";
    private final static String TEMP_DIR = "src/main/resources/files/temporary";

    @PostConstruct
    void init() {
        FileManageUtil.createDirIfNotExists(UPLOAD_DIR);
        FileManageUtil.createDirIfNotExists(TEMP_DIR);
    }

    public Path defaultBuildTempPath(String fileName) {
        return buildPath(fileName, TEMP_DIR);
    }

    public Path defaultBuildUploadPath(String fileName) {
        return buildPath(fileName, UPLOAD_DIR);
    }

    public Path buildPath(String imageName, String targetUrl) {
        return Paths.get(targetUrl).resolve(imageName).toAbsolutePath();
    }
}
