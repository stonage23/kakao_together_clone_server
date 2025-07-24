package com.kakao.together.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FileDto {
    private String fileName;
    private String filePath;
}
