package com.kakao.together.paymentgate.exception;

import retrofit2.HttpException;

public class PaymentValidationException extends RuntimeException {

    private int HttpStatusCode;

    public PaymentValidationException(String error, HttpException exception) {
        super(error, exception);
        this.HttpStatusCode = exception.code();
    }

    public int getHttpStatusCode() {
        return this.HttpStatusCode;
    }
}
