package com.kakao.together.domain.entity.content;

import lombok.Getter;

@Getter
public enum ContentType {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    TITLE("TITLE"),
    SUBTITLE("SUBTITLE");

    private String value;
    ContentType(final String value) {this.value = value;}
}