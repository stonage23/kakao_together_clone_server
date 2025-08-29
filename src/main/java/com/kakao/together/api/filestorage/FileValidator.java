package com.kakao.together.api.filestorage;

import org.springframework.web.multipart.MultipartFile;

public class FileValidator {

    private FileValidator() {
    }

    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "png", "gif", "docs", "pdf"};

    /**
     * 비교적 보안이 중요하지 않은 업로드 상황에서 사용할 검증 로직
     * @param fileName
     * @return
     */
    public static boolean isAllowedExtension(final String fileName) {
        if (fileName == null) return false;
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        for (String allowedExtension : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExtension)) return true;
        }
        return false;
    }

    /**
     * 보안이 필요한 업로드 상황에서 사용할 검증 로직
     * @param file
     * @return
     */
    public static boolean isAllowedExtension(final MultipartFile file) {
        return true;
    }
}
