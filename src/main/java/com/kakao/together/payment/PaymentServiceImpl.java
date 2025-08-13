package com.kakao.together.payment;

import com.kakao.together.controller.dto.PaymentDto.PaymentPendingDto;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.paymentgate.PaymentDetails;
import com.kakao.together.paymentgate.PaymentResponse;
import com.kakao.together.paymentgate.exception.PaymentNotFoundException;
import com.kakao.together.paymentgate.service.DefaultPaymentDetails;
import com.kakao.together.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentDetails loadPaymentByMerchantUid(String merchantUid) throws PaymentNotFoundException {
        PaymentTransaction paymentTransaction = paymentRepository.findByMerchantUid(merchantUid).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청하신 merchantUid에 해당하는 결제 내역이 존재하지 않습니다.")
        );

        return DefaultPaymentDetails.builder()
                .merchantUid(paymentTransaction.getMerchantUid())
                .amount(paymentTransaction.getAmount())
                .build();
    }

    @Override
    @Transactional
    public void completePayment(PaymentResponse payment) {
        PaymentTransaction paymentTransaction = paymentRepository.findByMerchantUid(payment.getMerchantUid()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청하신 merchantUid에 해당하는 결제 내역이 존재하지 않습니다.")
        );

        paymentTransaction.updateStatus(PaymentStatus.APPROVAL);

        switch (payment.getPayMethod()) {
            case "card" ->
                    paymentTransaction.setPaymentTransactionDetail(CardPaymentTransactionDetail.fromPaymentResponse(payment));
        }
    }

    @Override
    @Transactional
    public void updatePaymentCancellation(String merchantUid) {
        paymentRepository.findByMerchantUid(merchantUid).ifPresentOrElse(
                paymentTransaction -> paymentTransaction.updateStatus(PaymentStatus.CANCEL),
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청하신 merchantUid에 해당하는 결제 내역이 존재하지 않습니다.")
        );
    }

    @Override
    public void savePaymentAsPending(PaymentPendingDto requestDto) {
        paymentRepository.findByMerchantUid(requestDto.getMerchantUid()).ifPresentOrElse(
                paymentTransaction -> {
                    log.error("이미 존재하는 결제 내역 추가 시도");
                    if (paymentTransaction.getStatus() != PaymentStatus.PENDING) log.error("결제 내역 저장 중 보류상태가 아닌 결제내역이 이미 존재");
                    throw new CustomException(ErrorCode.DUPLICATE_PAYMENT);
                },
                () -> paymentRepository.save(requestDto.toEntity())
        );
    }
}
