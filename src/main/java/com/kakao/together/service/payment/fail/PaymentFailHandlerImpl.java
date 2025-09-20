package com.kakao.together.service.payment.fail;

import com.kakao.together.controller.paymentgate.dto.PaymentResponse;
import com.kakao.together.exception.payment.PaymentCancelException;
import com.kakao.together.exception.paymentgate.PaymentGateException;
import com.kakao.together.service.paymentgate.PaymentGateClient;
import com.kakao.together.service.payment.internal.PaymentInternalHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFailHandlerImpl implements PaymentFailHandler {

    private final PaymentGateClient paymentGateClient;
    private final PaymentInternalHandler paymentInternalHandler;

    private final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public void handle(String impUid) {

        log.debug("impUid: {}", impUid);

        PaymentResponse pgResponse;

        try {
            pgResponse = paymentGateClient.refundPayment(impUid);
        } catch (PaymentGateException e) {
            // TODO 서버 관리자에게 즉시 긴급 점검 알림 보내기
            throw new PaymentCancelException("paymentgate 통신에 문제가 생겨 자동 결제 취소 실패", e);
        }

        paymentInternalHandler.failPayment(pgResponse);
    }
}
