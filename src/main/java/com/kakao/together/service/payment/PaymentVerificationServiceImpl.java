package com.kakao.together.service.payment;

import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.exception.payment.PaymentCompleteException;
import com.kakao.together.exception.payment.PaymentVerificationException;
import com.kakao.together.service.payment.fail.PaymentFailHandler;
import com.kakao.together.service.payment.verification.PaymentVerificationHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentVerificationServiceImpl implements PaymentVerificationService {

    private final PaymentVerificationHandler paymentVerificationHandler123;
    private final PaymentFailHandler paymentFailHandler;

    private final Logger log = LoggerFactory.getLogger(Logger.class);

    @Override
    public void verifyPayment(String impUid) {
        try {
            paymentVerificationHandler123.verifyPayment(impUid);
        } catch (PaymentVerificationException e) {
            log.error("결제 검증에 실패하여 결제를 정상적으로 완료할 수 없습니다.", e);
            paymentFailHandler.handle(impUid);
            throw new CustomException(ErrorCode.FAILED_VERIFY_PAYMENT);
        } catch (PaymentCompleteException e) {
            log.error("결제 검증 이후 결제 완료 과정에서 문제가 발생했습니다.", e);
            paymentFailHandler.handle(impUid);
            throw new CustomException(ErrorCode.FAILED_COMPLETE_PAYMENT);
        }
    }
}
