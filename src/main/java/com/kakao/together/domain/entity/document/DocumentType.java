package com.kakao.together.domain.entity.document;

import lombok.Getter;

@Getter
public enum DocumentType {
    Story("story");

    private String value;
    DocumentType(String value) {this.value = value;}
}
