package com.kakao.together.external.paymentgate.service;

import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;

public interface PaymentGateClient {
    PaymentResponse getPayment(String impUid);

    PaymentResponse refundPayment(String impUid);
}
