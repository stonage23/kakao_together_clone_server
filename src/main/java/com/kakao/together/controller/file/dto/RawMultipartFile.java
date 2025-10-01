package com.kakao.together.controller.file.dto;

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
    private String contentType;
    private Long size;
}
