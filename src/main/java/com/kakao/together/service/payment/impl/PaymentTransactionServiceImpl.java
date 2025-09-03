package com.kakao.together.service.payment.impl;

import com.kakao.together.controller.payment.dto.PaymentDto.CardPaymentTransactionDetailResponse;
import com.kakao.together.controller.payment.dto.PaymentDto.PaymentPendingRequest;
import com.kakao.together.controller.payment.dto.PaymentDto.PaymentTransactionDetailResponse;
import com.kakao.together.domain.entity.payment.CardPaymentTransactionDetail;
import com.kakao.together.domain.entity.payment.PaymentStatus;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.domain.repository.PaymentTransactionRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.payment.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public void savePaymentAsPending(PaymentPendingRequest requestDto) {
        paymentTransactionRepository.findByMerchantUid(requestDto.getMerchantUid()).ifPresentOrElse(
                paymentTransaction -> {
                    log.error("이미 존재하는 결제 내역 추가 시도");
                    if (paymentTransaction.getStatus() != PaymentStatus.PENDING) log.error("결제 내역 저장 중 보류상태가 아닌 결제내역이 이미 존재");
                    throw new CustomException(ErrorCode.DUPLICATE_PAYMENT);
                },
                () -> paymentTransactionRepository.save(requestDto.toEntity())
        );
    }

    @Override
    public PaymentTransactionDetailResponse findOnlyApprovalPaymentTransaction(String merchantUid) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByMerchantUidAndStatus(merchantUid, PaymentStatus.APPROVAL).orElseThrow(
                () -> {
                    log.error("존재하지 않는 거래내역 또는 승인되지 않은 결제내역 조회 시도; merchantUid: {}", merchantUid);
                    throw new CustomException(ErrorCode.NOT_FOUND_ENTITY, "존재하지 않는 거래내역입니다.");
                }
        );
        if (paymentTransaction.getPaymentTransactionDetail() instanceof CardPaymentTransactionDetail cardPaymentTransactionDetail) {
            return CardPaymentTransactionDetailResponse.fromEntity(cardPaymentTransactionDetail);
        } else {
            log.error("정의된 결제 방식 어느 것에도 매칭되지 않아 발생한 예외; merchantUid: {}", merchantUid);
            throw new CustomException(ErrorCode.NOT_VALID_FORMAT, "정의되지 않은 결제 방식 에러");
        }
    }
}
