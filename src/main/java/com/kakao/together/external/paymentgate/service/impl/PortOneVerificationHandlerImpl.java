package com.kakao.together.external.paymentgate.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.exception.payment.PaymentVerificationException;
import com.kakao.together.external.paymentgate.service.PaymentGateClient;
import com.kakao.together.external.paymentgate.service.PaymentGateVerificationHandler;
import com.kakao.together.external.paymentgate.web.dto.PaymentDetails;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PortOneVerificationHandlerImpl implements PaymentGateVerificationHandler {

    private final PaymentGateClient paymentGateClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public PaymentResponse verify(String impUid, PaymentDetails paymentDetails) {

        log.debug("impUid: {}", impUid);
        log.info("payment gateway 결제 검증 시작");

        PaymentResponse pgResponse = paymentGateClient.getPayment(impUid);

        if (!Objects.equals(pgResponse.getAmount(), paymentDetails.getAmount())) {
            log.error("결제검증 금액 불일치. 서버에 저장된 결제금액: {}, 조회된 결제 금액: {}", paymentDetails.getAmount(), pgResponse.getAmount());
            throw new PaymentVerificationException("결제 금액이 일치하지 않음; impUid: " + impUid + ", merchantUid: " + paymentDetails.getMerchantUid());
        }

        log.info("payment gateway 결제 검증 성공");
        return pgResponse;
    }
}
