package com.kakao.together.service.payment.helper;

import com.kakao.together.domain.entity.payment.MerchnatUid;
import com.kakao.together.domain.entity.payment.PaymentType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MerchantUidProviderImpl implements MerchantUidProvider {

    @Override
    public String generateMerchantUid(PaymentType paymentType, String key) {
        return paymentType.getValue() + "_" + key + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    @Override
    public PaymentType extractPaymentType(String merchantUid) {
        if (merchantUid == null || merchantUid.isBlank()) {
            throw new IllegalArgumentException("merchantUid is null or empty");
        }
        MerchnatUid merchantUidObj = parseMerchantUid(merchantUid);
        return merchantUidObj.getType();
    }

    @Override
    public String extractKey(String merchantUid) {
        MerchnatUid merchantUidObj = parseMerchantUid(merchantUid);
        return merchantUidObj.getKey();
    }

    @Override
    public MerchnatUid parseMerchantUid(String merchantUid) {
        String[] values = merchantUid.split("_");
        return new MerchnatUid(values[0], values[1], values[2]);
    }
}
