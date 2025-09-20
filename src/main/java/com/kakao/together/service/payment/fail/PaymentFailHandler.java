package com.kakao.together.service.payment.fail;

public interface PaymentFailHandler {
    void handle(String impUid);
}
