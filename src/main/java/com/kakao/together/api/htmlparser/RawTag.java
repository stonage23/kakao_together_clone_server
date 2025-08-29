package com.kakao.together.api.htmlparser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class RawTag {
    private final String tagName;
    private final Map<String, String> attributes;
    private final String text;
}