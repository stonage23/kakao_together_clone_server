package com.kakao.together.domain.entity.fundraising;

import lombok.Getter;

@Getter
public enum FundraisingStatus {
    PAUSE("PAUSE"),
    ONGOING("ONGOING"),
    TEMPORARY("TEMPORARY"),
    CREATED("CREATED"),
    ENDED("ENDED");

    private final String status;
    FundraisingStatus(final String status) {this.status = status;}
}
