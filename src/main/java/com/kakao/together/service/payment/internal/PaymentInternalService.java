package com.kakao.together.service.payment.internal;

import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;

import java.time.Instant;

public interface PaymentInternalService {

    void completePayment(PaymentResponse paymentResponse);
    void failPayment(String merchantUid, String failReason, Instant failedAt);
}

