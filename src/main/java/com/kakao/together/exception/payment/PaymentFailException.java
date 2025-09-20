package com.kakao.together.exception.payment;

public class PaymentFailException extends PaymentException{
    public PaymentFailException(String message) {
        super(message);
    }

    public PaymentFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
