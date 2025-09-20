package com.kakao.together.service.payment.internal;

import com.kakao.together.controller.paymentgate.dto.PaymentResponse;

public interface PaymentInternalHandler {
    void completePayment(PaymentResponse paymentResponse);
    void failPayment(PaymentResponse pgResponse);
}

