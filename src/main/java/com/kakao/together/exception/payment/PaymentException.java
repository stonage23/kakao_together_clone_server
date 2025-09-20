package com.kakao.together.exception.payment;

public abstract class PaymentException extends RuntimeException{
    protected PaymentException(String message) {
        super(message);
    }
    protected PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
