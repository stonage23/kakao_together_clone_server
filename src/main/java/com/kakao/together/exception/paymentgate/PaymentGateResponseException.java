package com.kakao.together.exception.paymentgate;

public class PaymentGateResponseException extends PaymentGateException {

    public PaymentGateResponseException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateResponseException(String message) {
        super(message);
    }
}
