package com.kakao.together.exception.paymentgate;

public class PaymentNotFoundException extends PaymentGateException {
    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable e) {
        super(message, e);
    }
}
