package com.kakao.together.domain.entity.content;

import lombok.Getter;

@Getter
public enum ContentType {
    TEXT("text"),
    IMAGE("image"),
    TITLE("title"),
    SUBTITLE("subtitle");

    private String value;
    ContentType(final String value) {this.value = value;}
}