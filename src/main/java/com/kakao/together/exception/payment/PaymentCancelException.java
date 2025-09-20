package com.kakao.together.exception.payment;

public class PaymentCancelException extends PaymentException{
    public PaymentCancelException(String message) {
        super(message);
    }

    public PaymentCancelException(String message, Throwable cause) {
        super(message, cause);
    }
}
