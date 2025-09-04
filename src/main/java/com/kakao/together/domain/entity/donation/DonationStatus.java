package com.kakao.together.domain.entity.donation;

public enum DonationStatus {
    PENDING("PENDING"),
    COMPLETE("COMPLETE"),
    CANCELLED("CANCELLED");
    private String value;
    private DonationStatus(String value) {this.value = value;}

    public String getValue() {
        return this.value;
    }
}
