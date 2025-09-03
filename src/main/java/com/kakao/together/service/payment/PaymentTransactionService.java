package com.kakao.together.service.payment;

import com.kakao.together.controller.payment.dto.PaymentDto.PaymentPendingRequest;
import com.kakao.together.controller.payment.dto.PaymentDto.PaymentTransactionDetailResponse;

public interface PaymentTransactionService {
    void savePaymentAsPending(PaymentPendingRequest requestDto);

    PaymentTransactionDetailResponse findOnlyApprovalPaymentTransaction(String merchantUid);
}
