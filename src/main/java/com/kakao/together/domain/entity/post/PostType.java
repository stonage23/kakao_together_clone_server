package com.kakao.together.domain.entity.post;

import lombok.Getter;

@Getter
public enum PostType {
    STORY("STORY");

    private String value;
    PostType(String value) {this.value = value;}
}
