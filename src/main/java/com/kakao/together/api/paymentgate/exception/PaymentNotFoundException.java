package com.kakao.together.api.paymentgate.exception;

import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends PaymentGateBasicException {
    public PaymentNotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public PaymentNotFoundException(String message, HttpStatus httpStatus, Throwable e) {
        super(message, httpStatus, e);
    }
}
