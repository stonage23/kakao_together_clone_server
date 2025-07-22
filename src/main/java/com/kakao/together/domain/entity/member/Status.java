package com.kakao.together.domain.entity.member;

public enum Status {
    ACTIVE("ACTIVE"),
    DELETED("DELETED");

    private final String value;
    Status(final String value) {this.value = value;}
}
