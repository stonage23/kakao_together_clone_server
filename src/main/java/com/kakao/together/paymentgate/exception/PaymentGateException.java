package com.kakao.together.paymentgate.exception;

import org.springframework.http.HttpStatus;

public class PaymentGateException extends PaymentGateBasicException {

    public PaymentGateException(String message, HttpStatus httpStatus, Throwable e) {
        super(message, httpStatus, e);
    }

    public PaymentGateException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
