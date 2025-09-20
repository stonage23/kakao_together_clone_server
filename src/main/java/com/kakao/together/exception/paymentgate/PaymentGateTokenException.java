package com.kakao.together.exception.paymentgate;

public class PaymentGateTokenException extends PaymentGateException {
    public PaymentGateTokenException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateTokenException(String message) {
        super(message);
    }
}
