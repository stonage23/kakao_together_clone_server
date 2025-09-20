package com.kakao.together.exception.payment;

public class PaymentCompleteException extends PaymentException{
    public PaymentCompleteException(String message) {
        super(message);
    }

    public PaymentCompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
