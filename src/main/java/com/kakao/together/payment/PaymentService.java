package com.kakao.together.payment;

import com.kakao.together.controller.dto.PaymentDto.PaymentPendingDto;
import com.kakao.together.controller.dto.PaymentDto.PaymentTransactionDetailResponse;
import com.kakao.together.paymentgate.service.PaymentDetailsService;

public interface PaymentService extends PaymentDetailsService {
    void savePaymentAsPending(PaymentPendingDto requestDto);

    PaymentTransactionDetailResponse findOnlyApprovalPaymentTransaction(String merchantUid);
}
