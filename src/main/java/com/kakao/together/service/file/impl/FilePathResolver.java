package com.kakao.together.service.file.impl;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

@Component
public class FilePathResolver {

    private enum FileType {
        IMAGE("image/"),
        DOCUMENT("application/");

        private final String prefix;

        FileType(String prefix) {
            this.prefix = prefix;
        }

        public boolean matches(String contentType) {
            return contentType != null && contentType.startsWith(prefix);
        }
    }

    @Value("${storage.file.upload}")
    private String FILE_UPLOAD_DIR;
    @Value("${storage.file.temporary}")
    private String FILE_TEMP_DIR;
    @Value("${storage.image.upload}")
    private String IMAGE_UPLOAD_DIR;
    @Value("${storage.image.temporary}")
    private String IMAGE_TEMP_DIR;
    @Value("${storage.image.server}")
    private String IMAGE_SERVER_DIR;

    private final Map<FileType, String> uploadDirMap = new EnumMap<>(FileType.class);
    private final Map<FileType, String> tempDirMap = new EnumMap<>(FileType.class);
    private final Map<FileType, String> serverDirMap = new EnumMap<>(FileType.class);

    @PostConstruct
    void init() {
        uploadDirMap.put(FileType.IMAGE, IMAGE_UPLOAD_DIR);
        uploadDirMap.put(FileType.DOCUMENT, FILE_UPLOAD_DIR);

        tempDirMap.put(FileType.IMAGE, IMAGE_TEMP_DIR);
        tempDirMap.put(FileType.DOCUMENT, FILE_TEMP_DIR);

        serverDirMap.put(FileType.IMAGE, IMAGE_SERVER_DIR);

        // 디렉토리 보장
        uploadDirMap.values().forEach(FileManageUtil::createDirIfNotExists);
        tempDirMap.values().forEach(FileManageUtil::createDirIfNotExists);
    }

    /**
     *
     * @param fileName 확장자를 포함한 파일명
     * @param contentType
     * @return
     */
    public Path resolveUploadPath(String fileName, String contentType) {
        return resolveStoragePath(fileName, contentType, uploadDirMap);
    }

    /**
     *
     * @param fileName 확장자를 포함한 파일명
     * @param contentType
     * @return
     */
    public Path resolveTempPath(String fileName, String contentType) {
        return resolveStoragePath(fileName, contentType, tempDirMap);
    }

    public String resolveServerPath(String fileName, String contentType) {
        return resolveServerPath(fileName, contentType, serverDirMap);
    }

    private Path resolveStoragePath(String fileName, String contentType, Map<FileType, String> dirMap) {
        return dirMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(contentType))
                .findFirst()
                .map(entry -> buildAbsolutePath(fileName, entry.getValue()))
                .orElseThrow(() -> new CustomException(
                        ErrorCode.UNSUPPORTED_FILE_FORMAT,
                        "업로드하려는 파일의 contentType을 확인해주세요; contentType=" + contentType + "; fileName:" + fileName
                ));
    }

    private String resolveServerPath(String fileName, String contentType, Map<FileType, String> dirMap) {
        return dirMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(contentType))
                .findFirst()
                .map(entry -> buildPath(fileName, entry.getValue()))
                .orElseThrow(() -> new CustomException(
                        ErrorCode.UNSUPPORTED_FILE_FORMAT,
                        "업로드하려는 파일의 contentType을 확인해주세요; contentType=" + contentType + "; fileName:" + fileName
                ));
    }

    private Path buildAbsolutePath(String fileName, String baseDir) {
        return Paths.get(baseDir).resolve(fileName).toAbsolutePath();
    }

    private String buildPath(String fileName, String baseDir) {
        return baseDir + "/" + fileName;
    }
}

