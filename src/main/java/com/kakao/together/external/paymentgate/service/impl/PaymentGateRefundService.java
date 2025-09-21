package com.kakao.together.external.paymentgate.service.impl;

import com.kakao.together.external.paymentgate.service.PaymentGateClient;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import com.kakao.together.external.paymentgate.exception.PaymentGateCancelException;
import com.kakao.together.external.paymentgate.exception.PaymentGateResponseException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGateRefundService {

    private final PaymentGateClient paymentGateClient;
    private final Logger log = LoggerFactory.getLogger(Logger.class);

    public PaymentResponse refundPayment(String impUid) {

        log.info("payment gate 환불 로직 시작");

        PaymentResponse pgResponse = null;

        try {
            pgResponse = paymentGateClient.refundPayment(impUid);
        } catch (PaymentGateResponseException e) {
            throw new PaymentGateCancelException("payment gate 환불 처리에 실패했습니다.", e);
        }

        log.info("payment gate 환불 성공");
        return pgResponse;
    }
}
