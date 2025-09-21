package com.kakao.together.external.paymentgate.exception;

public class PaymentGateResponseException extends PaymentGateException {

    public PaymentGateResponseException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateResponseException(String message) {
        super(message);
    }
}
