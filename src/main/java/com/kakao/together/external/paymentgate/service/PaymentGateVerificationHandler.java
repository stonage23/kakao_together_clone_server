package com.kakao.together.external.paymentgate.service;

import com.kakao.together.external.paymentgate.web.dto.PaymentDetails;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;

public interface PaymentGateVerificationHandler {
    PaymentResponse verify(String impUid, PaymentDetails paymentDetails);
}
