package com.kakao.together.external.paymentgate.exception;

public abstract class PaymentGateException extends RuntimeException {

    protected PaymentGateException(String message, Throwable e) {
        super(message, e);
    }

    protected PaymentGateException(String message) {
        super(message);
    }
}
