package com.kakao.together.domain.entity.donation;

public enum DonationStatus {
    PENDING("PENDING"),
    COMPLETE("COMPLETE"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED"),
    REQUEST_CANCEL("REQUEST_CANCEL"),
    FAILED_CANCEL("FAILED_CANCEL");
    private String value;
    DonationStatus(String value) {this.value = value;}

    public String getValue() {
        return this.value;
    }
}
