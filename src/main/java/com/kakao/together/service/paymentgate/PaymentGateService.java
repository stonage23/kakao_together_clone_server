package com.kakao.together.service.paymentgate;

public interface PaymentGateService {
    void verifyPayment(String impUid);

    void refundPayment(String impUid);
}
