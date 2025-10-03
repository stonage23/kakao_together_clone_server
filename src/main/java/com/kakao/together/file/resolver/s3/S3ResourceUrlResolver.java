package com.kakao.together.file.resolver.s3;

import com.kakao.together.file.helper.LogicalPathMapper;
import com.kakao.together.file.resolver.ResourceUrlResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("s3")
@Slf4j
public class S3ResourceUrlResolver implements ResourceUrlResolver {

    private final LogicalPathMapper logicalPathMapper;

    @Value("${s3.root-url}")
    private String ROOT_URL;

    @Override
    public String resolveTempUrl(String fileName, String contentType) {
        String prefixPath = logicalPathMapper.getTempPrefix(contentType);
        return ROOT_URL + "/" + prefixPath + "/" + fileName;
    }

    @Override
    public String resolveUploadUrl(String fileName, String contentType) {
        String prefixPath = logicalPathMapper.getUploadPrefix(contentType);
        return ROOT_URL + "/" + prefixPath + "/" + fileName;
    }
}

