package com.kakao.together.api.paymentgate.exception;

public class PaymentGateTokenException extends PaymentGateBasicException{
    public PaymentGateTokenException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateTokenException(String message) {
        super(message);
    }
}
