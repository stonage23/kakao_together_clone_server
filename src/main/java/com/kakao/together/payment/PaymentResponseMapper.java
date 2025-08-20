package com.kakao.together.payment;

import com.kakao.together.api.paymentgate.PaymentResponse;

public interface PaymentResponseMapper<T> {
    PaymentResponse toPaymentResponse(T source);
}
