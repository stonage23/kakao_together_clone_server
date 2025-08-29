package com.kakao.together.api.filestorage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class RawMultipartFile {
    private String originalFilename;
    private String savedFileName;
    private String extension;
    private String url;
    private String contentType;
    private Long size;
}
