package com.kakao.together.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ImageDto {

    private String originalName;
    private String savedPath;
}
