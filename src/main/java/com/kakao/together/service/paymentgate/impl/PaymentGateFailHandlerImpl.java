package com.kakao.together.service.paymentgate.impl;

import com.kakao.together.external.paymentgate.exception.PaymentGateResponseException;
import com.kakao.together.external.paymentgate.service.PaymentGateClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGateFailHandlerImpl implements PaymentGateFailHandler {

    private final PaymentGateClient paymentGateClient;
    private final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public void handle(String impUid, String merchantUid) {

        log.debug("impUid: {}", impUid);
        log.info("결제 검증 실패 로직 시작");

        try {
            paymentGateClient.refundPayment(impUid);
        } catch (PaymentGateResponseException e) {
            // TODO 결제 검증 실패에 따른 자동 환불처리 실패. 서버 관리자에게 즉시 알림
            log.info("결제 검증 실패 로직 실패");
            log.error("결제 환불 처리에 실패하여 즉각적이 조치 필요. impUid= " + impUid + ", merchantUid= " + merchantUid, e.getMessage());
            return;
        }

        log.info("결제 검증 실패 로직 성공");
    }
}
