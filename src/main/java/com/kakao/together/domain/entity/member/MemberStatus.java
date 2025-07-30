package com.kakao.together.domain.entity.member;

public enum MemberStatus {
    ACTIVE("ACTIVE"),
    DELETED("DELETED"),
    LOCKED("LOCKED");

    private final String value;
    MemberStatus(final String value) {this.value = value;}
}
