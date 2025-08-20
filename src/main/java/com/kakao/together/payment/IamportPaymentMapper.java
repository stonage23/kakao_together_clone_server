package com.kakao.together.payment;

import com.kakao.together.api.paymentgate.PaymentResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.stereotype.Component;

@Component
public class IamportPaymentMapper implements PaymentResponseMapper<Payment> {
    @Override
    public PaymentResponse toPaymentResponse(Payment source) {
        return PaymentResponse.builder()
                .merchantUid(source.getMerchantUid())
                .impUid(source.getImpUid())
                .amount(source.getAmount())
                .payMethod(source.getPayMethod())
                .pgProvider(source.getPgProvider())
                .bankCode(source.getBankCode())
                .bankName(source.getBankName())
                .cardCode(source.getCardCode())
                .cardName(source.getCardName())
                .cardNumber(source.getCardNumber())
                .cardType(source.getCardType())
                .currency(source.getCurrency())
                .buyerName(source.getBuyerName())
                .buyerEmail(source.getBuyerEmail())
                .buyerTel(source.getBuyerTel())
                .paidAt(source.getPaidAt().toInstant())
                .failAt(source.getFailedAt().toInstant())
                .cancelledAt(source.getCancelledAt().toInstant())
                .build();
    }
}
