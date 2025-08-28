package com.kakao.together.domain.entity.content;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ContentType {
    TEXT("TEXT", "p"),
    IMAGE("IMAGE", "img"),
    SUBTITLE("SUBTITLE", "h2");

    private final String value;
    private final String tag;

    ContentType(String value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    public static ContentType fromTag(String tag) {
        for (ContentType type : ContentType.values()) {
            if (type.tag.equalsIgnoreCase(tag)) {
                return type;
            }
        }
        throw new CustomException(ErrorCode.NOT_VALID_TAG);
    }
}