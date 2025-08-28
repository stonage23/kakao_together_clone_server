package com.kakao.together.service.file.impl;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.PostConstruct;
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

    private static final String FILE_UPLOAD_DIR = "src/main/resources/files/content";
    private static final String FILE_TEMP_DIR = "src/main/resources/files/temporary";
    private static final String IMAGE_UPLOAD_DIR = "src/main/resources/imgs/content";
    private static final String IMAGE_TEMP_DIR = "src/main/resources/imgs/temporary";

    private final Map<FileType, String> uploadDirMap = new EnumMap<>(FileType.class);
    private final Map<FileType, String> tempDirMap = new EnumMap<>(FileType.class);

    @PostConstruct
    void init() {
        uploadDirMap.put(FileType.IMAGE, IMAGE_UPLOAD_DIR);
        uploadDirMap.put(FileType.DOCUMENT, FILE_UPLOAD_DIR);

        tempDirMap.put(FileType.IMAGE, IMAGE_TEMP_DIR);
        tempDirMap.put(FileType.DOCUMENT, FILE_TEMP_DIR);

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
        return resolvePath(fileName, contentType, uploadDirMap);
    }

    /**
     *
     * @param fileName 확장자를 포함한 파일명
     * @param contentType
     * @return
     */
    public Path resolveTempPath(String fileName, String contentType) {
        return resolvePath(fileName, contentType, tempDirMap);
    }

    private Path resolvePath(String fileName, String contentType, Map<FileType, String> dirMap) {
        return dirMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(contentType))
                .findFirst()
                .map(entry -> buildPath(fileName, entry.getValue()))
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_VALID_FORMAT,
                        "업로드하려는 파일의 contentType을 확인해주세요; contentType=" + contentType + "; fileName:" + fileName
                ));
    }

    private Path buildPath(String fileName, String baseDir) {
        return Paths.get(baseDir).resolve(fileName).toAbsolutePath();
    }
}

