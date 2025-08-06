package com.kakao.together.payment;

public enum PaymentStatus {
    PENDING("PENDING"),
    APPROVAL("APPROVAL"),
    CANCEL("CANCEL");

    String status;
    PaymentStatus(String status) {this.status = status;}
}
