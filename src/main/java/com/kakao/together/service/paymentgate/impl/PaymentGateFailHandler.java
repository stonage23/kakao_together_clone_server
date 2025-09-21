package com.kakao.together.service.paymentgate.impl;

public interface PaymentGateFailHandler {
    void handle(String impUid, String merchantUid);
}
