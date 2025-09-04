package com.kakao.together.api.paymentgate.exception;

import java.io.IOException;

public abstract class PaymentGateBasicException extends IOException {

    public PaymentGateBasicException(String message, Throwable e) {
        super(message, e);
    }

    public PaymentGateBasicException(String message) {
        super(message);
    }
}
