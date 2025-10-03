package com.kakao.together.file.resolver;

public interface ResourceUrlResolver {
    String resolveTempUrl(String fileName, String contentType);

    String resolveUploadUrl(String fileName, String contentType);
}
