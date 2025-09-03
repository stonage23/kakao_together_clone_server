package com.kakao.together.domain.entity.payment;

public enum PaymentStatus {
    PENDING("PENDING"),
    APPROVAL("APPROVAL"),
    CANCEL("CANCEL");

    String status;
    PaymentStatus(String status) {this.status = status;}
}
