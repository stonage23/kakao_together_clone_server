package com.kakao.together.paymentgate.exception;

import org.springframework.http.HttpStatus;

public abstract class PaymentGateBasicException extends RuntimeException {
    private String message;
    private int httpStatusCode;

    public PaymentGateBasicException(String message, HttpStatus httpStatus, Throwable e) {
        super(message, e);
        this.message = message;
        this.httpStatusCode = httpStatus.value();
    }

    public PaymentGateBasicException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatusCode = httpStatus.value();
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}
