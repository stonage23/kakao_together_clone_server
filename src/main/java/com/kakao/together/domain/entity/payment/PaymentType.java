package com.kakao.together.domain.entity.payment;

public enum PaymentType {
    DONATION("DONATION");

    private final String value;

    PaymentType(final String value) { this.value = value; }
    public String getValue() { return value; }
}
