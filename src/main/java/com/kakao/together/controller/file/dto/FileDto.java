package com.kakao.together.controller.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class FileDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class FileResponse {
        private Long id;
        private String originalName;
        private String url;
        private Long size;
        private String contentType;
    }
}
