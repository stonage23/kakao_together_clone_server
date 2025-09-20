package com.kakao.together.service.payment.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.controller.paymentgate.dto.PaymentDetails;
import com.kakao.together.controller.paymentgate.dto.PaymentResponse;
import com.kakao.together.exception.payment.PaymentVerificationException;
import com.kakao.together.exception.paymentgate.PaymentGateException;
import com.kakao.together.service.payment.details.PaymentDetailsService;
import com.kakao.together.service.paymentgate.PaymentGateClient;
import com.kakao.together.service.payment.internal.PaymentInternalHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentVerificationHandlerImpl implements PaymentVerificationHandler {

    private final PaymentDetailsService paymentDetailsService;
    private final PaymentGateClient paymentGateClient;
    private final PaymentInternalHandler paymentInternalHandler;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public void verifyPayment(String impUid) {

        log.debug("impUid: {}", impUid);

        PaymentResponse pgResponse;

        try {
            pgResponse = paymentGateClient.getPayment(impUid);

            PaymentDetails paymentDetails = null;
            String merchantUid = pgResponse.getMerchantUid();
            paymentDetails = paymentDetailsService.loadPaymentByMerchantUid(merchantUid);

            if (!Objects.equals(pgResponse.getAmount(), paymentDetails.getAmount())) {
                throw new PaymentVerificationException("결제 금액이 일치하지 않음; impUid: " + impUid + ", merchantUid: " + paymentDetails.getMerchantUid());
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new PaymentVerificationException("결제 검증 도중 문제가 발생", e);
        } catch (PaymentGateException e) {
            throw new PaymentVerificationException("paymentgate 통신에 문제가 생겨 결제 검증 실패", e);
        }
        paymentInternalHandler.completePayment(pgResponse);
    }
}
