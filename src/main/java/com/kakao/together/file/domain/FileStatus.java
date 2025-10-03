package com.kakao.together.file.domain;

public enum FileStatus {
    PENDING("PENDING"),
    USED("USED"),
    DELETED("DELETED");

    private String value;

    FileStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
