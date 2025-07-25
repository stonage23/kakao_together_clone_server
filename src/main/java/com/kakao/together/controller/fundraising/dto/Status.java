package com.kakao.together.controller.fundraising.dto;

import lombok.Getter;

@Getter
public enum Status {
    TEMPORARY("TEMPORARY"),
    CREATED("CREATED"),
    ENDED("ENDED");

    private final String status;
    Status(final String status) {this.status = status;}
}
