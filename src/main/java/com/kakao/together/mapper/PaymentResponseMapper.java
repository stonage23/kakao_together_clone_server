package com.kakao.together.mapper;

import com.kakao.together.controller.paymentgate.dto.PaymentResponse;

public interface PaymentResponseMapper<T> {
    PaymentResponse toPaymentResponse(T source);
}
