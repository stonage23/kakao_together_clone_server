package com.kakao.together.api.paymentgate.exception;

public class PaymentNotFoundException extends PaymentGateBasicException {
    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable e) {
        super(message, e);
    }
}
