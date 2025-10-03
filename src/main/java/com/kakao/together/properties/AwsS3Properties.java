package com.kakao.together.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cloud.aws")
@Getter
public class AwsS3Properties {

    private final Credentials credentials;
    private final S3 s3;
    private final Region region;

    @Getter
    @RequiredArgsConstructor
    public static class Credentials {
        private final String accessKey;
        private final String secretKey;
    }

    @RequiredArgsConstructor
    @Getter
    public static class S3 {
        private final String bucket;
    }

    @RequiredArgsConstructor
    @Getter
    public static class Region {
        private final String statics;
    }
}
