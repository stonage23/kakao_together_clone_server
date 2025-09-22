package com.kakao.together.service.paymentgate.impl;

import com.kakao.together.exception.payment.PaymentCompleteException;
import com.kakao.together.exception.payment.PaymentVerificationException;
import com.kakao.together.external.paymentgate.exception.PaymentGateException;
import com.kakao.together.external.paymentgate.service.PaymentGateVerificationHandler;
import com.kakao.together.external.paymentgate.verification.PaymentVerificationService;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import com.kakao.together.service.payment.internal.PaymentInternalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PortOneVerificationService implements PaymentVerificationService {

    private final PaymentGateVerificationHandler paymentGateVerificationHandler;
    private final PaymentInternalService paymentInternalService;
    private final PaymentGateFailHandler paymentGateFailHandler;

    private final Logger log = LoggerFactory.getLogger(Logger.class);

    /**
     * false 반환하는 경우는 pg사에서 일정 시간 지난 후 다시 결제검증 웹훅 보내도록 하는 경우이며 서버 내부 상태변화 없음.
     * @param impUid
     * @param merchantUid
     * @return
     */
    @Override
    public boolean verifyPayment(String impUid, String merchantUid) {

        PaymentResponse pgResponse = null;

        try {
            pgResponse = paymentGateVerificationHandler.handle(impUid);
        } catch (PaymentGateException e) {
            log.warn("결제 검증을 위한 외부 pg사에게 적절한응답을 받지 못했습니다. impUid = " + impUid + ", merchantUid= " + merchantUid, e);
            return false;
        } catch (PaymentVerificationException e) {
            log.warn("결제 검증에 실패하여 결제를 정상적으로 완료할 수 없습니다.", e);
            paymentInternalService.failPayment(merchantUid, e.getMessage(), Instant.now());
            paymentGateFailHandler.handle(impUid, merchantUid);
            return true;
        }

        try {
            paymentInternalService.completePayment(pgResponse);
        } catch (PaymentCompleteException e) {
            log.warn("결제 검증 성공 이후 서버 내부 상태 변경 중 문제가 발생했습니다.", e);
            return true;
        }

        return true;
    }
}
