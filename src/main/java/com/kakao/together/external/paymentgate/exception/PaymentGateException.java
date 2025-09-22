package com.kakao.together.external.paymentgate.exception;

public abstract class PaymentGateException extends RuntimeException {

    public PaymentGateException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateException(String message) {
        super(message);
    }
}
