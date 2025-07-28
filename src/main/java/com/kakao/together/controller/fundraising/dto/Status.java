package com.kakao.together.controller.fundraising.dto;

import lombok.Getter;

@Getter
public enum Status {
    PAUSE("PAUSE"),
    ONGOING("ONGOING"),
    TEMPORARY("TEMPORARY"),
    CREATED("CREATED"),
    ENDED("ENDED");

    private final String status;
    Status(final String status) {this.status = status;}
}
