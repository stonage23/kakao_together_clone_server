package com.kakao.together.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FileManageUtil {

    public void createDirIfNotExists(String dirName) {
        try {
            Path path = Paths.get(dirName).toAbsolutePath();
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException("해당 위치에 동일한 이름의 파일이 이미 존재", e);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 디렉토리 생성 실패", e);
        }
    }
}
