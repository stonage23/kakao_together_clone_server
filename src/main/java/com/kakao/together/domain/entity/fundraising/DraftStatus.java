package com.kakao.together.domain.entity.fundraising;

import lombok.Getter;

@Getter
public enum DraftStatus {
    DRAFT("DRAFT"),
    PUBLISHED("PUBLISHED");

    private final String value;

    DraftStatus(final String value) {
        this.value = value;
    }
}
