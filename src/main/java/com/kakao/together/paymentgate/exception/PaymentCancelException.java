package com.kakao.together.paymentgate.exception;

import org.springframework.http.HttpStatus;

public class PaymentCancelException extends PaymentGateBasicException {

    public PaymentCancelException(String message, HttpStatus httpStatus, Throwable e) {
        super(message, httpStatus, e);
    }

    public PaymentCancelException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
