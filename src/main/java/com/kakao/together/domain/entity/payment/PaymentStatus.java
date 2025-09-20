package com.kakao.together.domain.entity.payment;

public enum PaymentStatus {
    PENDING("PENDING"),
    APPROVAL("APPROVAL"),
    CANCEL("CANCEL"),
    FAILED("FAILED"),
    FAILED_CANCEL("FAILED_CANCEL");

    String status;
    PaymentStatus(String status) {this.status = status;}

    public String getStatus() { return status; }
}
