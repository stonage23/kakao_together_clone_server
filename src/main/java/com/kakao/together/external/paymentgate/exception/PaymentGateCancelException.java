package com.kakao.together.external.paymentgate.exception;

public class PaymentGateCancelException extends PaymentGateException{
    public PaymentGateCancelException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateCancelException(String message) {
        super(message);
    }
}
