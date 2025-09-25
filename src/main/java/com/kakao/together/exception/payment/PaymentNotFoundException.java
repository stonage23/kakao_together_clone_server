package com.kakao.together.exception.payment;

public class PaymentNotFoundException extends PaymentVerificationException {
    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
