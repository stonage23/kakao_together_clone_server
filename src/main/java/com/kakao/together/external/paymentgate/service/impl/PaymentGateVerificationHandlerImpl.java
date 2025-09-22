package com.kakao.together.external.paymentgate.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.exception.payment.PaymentVerificationException;
import com.kakao.together.external.paymentgate.exception.PaymentGateResponseException;
import com.kakao.together.external.paymentgate.service.PaymentDetailsService;
import com.kakao.together.external.paymentgate.service.PaymentGateClient;
import com.kakao.together.external.paymentgate.service.PaymentGateVerificationHandler;
import com.kakao.together.external.paymentgate.web.dto.PaymentDetails;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentGateVerificationHandlerImpl implements PaymentGateVerificationHandler {

    private final PaymentDetailsService paymentDetailsService;
    private final PaymentGateClient paymentGateClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public PaymentResponse handle(String impUid) {

        log.debug("impUid: {}", impUid);
        log.info("payment gateway 결제 검증 시작");

        PaymentResponse pgResponse;

        try {
            pgResponse = paymentGateClient.getPayment(impUid);

            PaymentDetails paymentDetails = null;
            String merchantUid = pgResponse.getMerchantUid();
            paymentDetails = paymentDetailsService.loadPaymentByMerchantUid(merchantUid);

            if (!Objects.equals(pgResponse.getAmount(), paymentDetails.getAmount())) {
                throw new PaymentVerificationException("결제 금액이 일치하지 않음; impUid: " + impUid + ", merchantUid: " + paymentDetails.getMerchantUid());
            }
        } catch (IllegalArgumentException | NoSuchElementException | PaymentGateResponseException e) {
            throw new PaymentVerificationException("결제 검증 실패", e);
        }

        log.info("payment gateway 결제 검증 성공");
        return pgResponse;
    }
}
