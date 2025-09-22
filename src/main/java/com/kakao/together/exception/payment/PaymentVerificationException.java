package com.kakao.together.exception.payment;

public class PaymentVerificationException extends PaymentException{

    public PaymentVerificationException(String message) {
        super(message);
    }

    public PaymentVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
