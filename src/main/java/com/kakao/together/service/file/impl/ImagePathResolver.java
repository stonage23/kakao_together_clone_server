package com.kakao.together.service.file.impl;

import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImagePathResolver {

    private static final String UPLOAD_DIR = "src/main/resources/imgs/content";
    private static final String TEMP_DIR = "src/main/resources/imgs/temporary";

    @PostConstruct
    void init() {
        FileManageUtil.createDirIfNotExists(UPLOAD_DIR);
        FileManageUtil.createDirIfNotExists(TEMP_DIR);
    }

    public Path defaultBuildTempPath(String imageName) {
        return buildPath(imageName, TEMP_DIR);
    }

    public Path defaultBuildUploadPath(String imageName) {
        return buildPath(imageName, UPLOAD_DIR);
    }

    public Path buildPath(String imageName, String targetUrl) {
        return Paths.get(targetUrl).resolve(imageName).toAbsolutePath();
    }
}
