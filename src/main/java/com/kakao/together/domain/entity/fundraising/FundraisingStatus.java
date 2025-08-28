package com.kakao.together.domain.entity.fundraising;

public enum FundraisingStatus {
    ONGOING("ONGOING"),
    PAUSE("PAUSE"),
    ENDED("ENDED");

    private final String value;

    FundraisingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
