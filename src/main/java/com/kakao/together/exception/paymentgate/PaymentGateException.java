package com.kakao.together.exception.paymentgate;

public abstract class PaymentGateException extends RuntimeException {

    public PaymentGateException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateException(String message) {
        super(message);
    }
}
