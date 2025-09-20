package com.kakao.together.service.paymentgate;

import com.kakao.together.controller.paymentgate.dto.PaymentResponse;

public interface PaymentGateClient {
    PaymentResponse getPayment(String impUid);

    PaymentResponse refundPayment(String impUid);
}
