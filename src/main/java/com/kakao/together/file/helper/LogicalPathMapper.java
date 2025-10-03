package com.kakao.together.file.helper;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.util.FileManageUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@Slf4j
public class LogicalPathMapper {

    @PostConstruct
    void init() {

        pathConfigMap.put(FileType.IMAGE, new PathConfig(IMAGE_TEMP_DIR, IMAGE_UPLOAD_DIR));
        pathConfigMap.put(FileType.DOCUMENT, new PathConfig(FILE_TEMP_DIR, FILE_UPLOAD_DIR));

        pathConfigMap.values().stream()
                .flatMap(pathConfig -> Stream.of(pathConfig.tempDir, pathConfig.uploadDir))
                .filter(Objects::nonNull)
                .distinct()
                .forEach(FileManageUtil::createDirIfNotExists);
    }


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

        public static Optional<FileType> of(String contentType) {
            return Stream.of(values())
                    .filter(type -> type.matches(contentType))
                    .findFirst();
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

    private record PathConfig(String tempDir, String uploadDir) {
    }

    private final Map<FileType, PathConfig> pathConfigMap = new EnumMap<>(FileType.class);

    public String getUploadPrefix(String contentType) {
        return resolvePath(contentType, PathConfig::uploadDir);
    }

    public String getTempPrefix(String contentType) {
        return resolvePath(contentType, PathConfig::tempDir);
    }

    private String resolvePath(String contentType, Function<PathConfig, String> dirExtractor) {

        PathConfig config = FileType.of(contentType)
                .map(pathConfigMap::get)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.UNSUPPORTED_FILE_FORMAT,
                        "지원하지 않는 파일 형식입니다; contentType=" + contentType
                ));

        String logicalPath = dirExtractor.apply(config);
        if (logicalPath == null) {
            log.warn("해당 파일 형식에 대한 서버 경로가 설정되지 않았습니다; contentType=" + contentType);
            throw new CustomException(ErrorCode.FAILED_URL_CREATION);
        }

        return logicalPath;
    }
}
