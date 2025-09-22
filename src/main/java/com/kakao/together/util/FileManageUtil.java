package com.kakao.together.util;

import com.kakao.together.exception.CustomException;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FileManageUtil {

    public void createDirIfNotExists(String dirName) {
        try {
            Path path = Paths.get(dirName).toAbsolutePath();
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (SecurityException | IOException e) {
            throw new CustomException(e);
        }
    }
}
