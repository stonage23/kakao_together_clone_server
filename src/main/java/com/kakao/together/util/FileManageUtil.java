package com.kakao.together.util;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@UtilityClass
@Slf4j
public class FileManageUtil {

    public void createDirIfNotExists(String dirName) {
        try {
            Path path = Paths.get(dirName).toAbsolutePath();
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (SecurityException | IOException e) {
            throw new CustomException(e);
        }
    }

    public String extractExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(ofn -> ofn.contains("."))
                .map(ofn -> ofn.substring(fileName.lastIndexOf(".")))
                .filter(ofn -> ofn.length() != 0)
                .orElseThrow(() -> {
                    log.info("파일형식이 존재하지 않는 파일. filename: {}", fileName);
                    return new CustomException(ErrorCode.UNSUPPORTED_FILE_FORMAT);
                });
    }
}
