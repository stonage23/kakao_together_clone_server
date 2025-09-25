package com.kakao.together.external.paymentgate.verification;

public interface PaymentVerificationService {
    void verifyPayment(String impUid, String merchantUid);
}
