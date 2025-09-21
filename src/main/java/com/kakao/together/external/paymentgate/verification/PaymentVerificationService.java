package com.kakao.together.external.paymentgate.verification;

public interface PaymentVerificationService {
    boolean verifyPayment(String impUid, String merchantUid);
}
