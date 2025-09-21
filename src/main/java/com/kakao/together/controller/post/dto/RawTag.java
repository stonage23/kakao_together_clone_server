package com.kakao.together.controller.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class RawTag {
    private final String tagName;
    private final Map<String, String> attributes;
    private final String text;
    private final String innerHtml;
}