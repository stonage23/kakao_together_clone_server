package com.kakao.together.service.paymentgate.impl;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.exception.payment.PaymentCompleteException;
import com.kakao.together.exception.payment.PaymentNotFoundException;
import com.kakao.together.exception.payment.PaymentVerificationException;
import com.kakao.together.external.paymentgate.exception.PaymentGateException;
import com.kakao.together.external.paymentgate.service.PaymentDetailsService;
import com.kakao.together.external.paymentgate.service.PaymentGateVerificationHandler;
import com.kakao.together.external.paymentgate.verification.PaymentVerificationService;
import com.kakao.together.external.paymentgate.web.dto.PaymentDetails;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import com.kakao.together.service.payment.internal.PaymentInternalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PortOneVerificationService implements PaymentVerificationService {

    private final PaymentGateVerificationHandler paymentGateVerificationHandler;
    private final PaymentDetailsService paymentDetailsService;
    private final PaymentInternalService paymentInternalService;

    private final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public void verifyPayment(String impUid, String merchantUid) {

        PaymentResponse pgResponse = null;
        PaymentDetails paymentDetails = null;

        try {

            paymentDetails = paymentDetailsService.loadPaymentByMerchantUid(merchantUid);

            if (!paymentDetails.isRequiredVerification()) {
                log.info("이미 처리된 결제내역. impUid: {}, merchantUid: {}", impUid, merchantUid);
                return;
            }

        } catch(NoSuchElementException e) {
            throw new PaymentNotFoundException("merchantUid에 해당하는 결제내역이 서버에 존재하지 않습니다. merchantUid: " + merchantUid);
        }

        try {

            pgResponse = paymentGateVerificationHandler.verify(impUid, paymentDetails);

            paymentInternalService.completePayment(pgResponse);


        } catch (PaymentGateException e) {
            log.warn("결제 검증을 위한 외부 pg사에게 적절한응답을 받지 못했습니다. impUid = " + impUid + ", merchantUid= " + merchantUid, e);
            throw new CustomException(ErrorCode.BAD_PG_RESPONSE);
        } catch (PaymentVerificationException e) {
            log.error("결제 검증에 실패하여 결제를 정상적으로 완료할 수 없습니다. impUid = " + impUid + ", merchantUid= " + merchantUid, e);
            paymentInternalService.failPayment(merchantUid, e.getMessage(), Instant.now());
            throw new CustomException(ErrorCode.FAILED_VERIFY_PAYMENT);
        } catch (PaymentCompleteException e) {
            log.error("결제 검증 성공 이후 서버 내부 상태 변경 중 문제가 발생했습니다. impUid = " + impUid + ", merchantUid= " + merchantUid, e);
            throw new CustomException(ErrorCode.FAILED_COMPLETE_PAYMENT);
        }
    }
}
