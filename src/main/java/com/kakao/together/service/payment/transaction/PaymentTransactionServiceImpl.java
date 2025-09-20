package com.kakao.together.service.payment.transaction;

import com.kakao.together.domain.entity.payment.PaymentStatus;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.domain.repository.PaymentTransactionRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentTransaction getPaymentTransaction(String merchantUid) {
        return paymentTransactionRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new NoSuchElementException("not present paymentTransaction"));
    }

    @Override
    @Transactional
    public void savePaymentAsPending(String merchantUid, Long amount) {
        paymentTransactionRepository.findByMerchantUid(merchantUid).ifPresentOrElse(
                paymentTransaction -> {
                    log.error("이미 존재하는 결제 내역 추가 시도");
                    if (paymentTransaction.getStatus() != PaymentStatus.PENDING) log.error("결제 내역 저장 중 보류상태가 아닌 결제내역이 이미 존재");
                    throw new CustomException(ErrorCode.DUPLICATE_PAYMENT);
                },
                () -> {
                    PaymentTransaction paymentTransaction = PaymentTransaction.builder()
                            .merchantUid(merchantUid)
                            .amount(amount)
                            .build();
                    paymentTransactionRepository.save(paymentTransaction);
                }
        );
    }

    @Override
    @Transactional
    public void cancelPayment(Long paymentTransactionId, Long cancelledAt) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(paymentTransactionId)
                .orElseThrow(() -> new NoSuchElementException("not present paymentTransaction"));
        paymentTransaction.cancelPayment(cancelledAt);
    }
}
