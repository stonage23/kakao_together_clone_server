package com.kakao.together.helper;

import com.kakao.together.domain.entity.payment.MerchnatUid;
import com.kakao.together.domain.entity.payment.PaymentType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MerchantUidProvider {

    public String generateMerchantUid(PaymentType paymentType, String key) {
        return paymentType.getValue() + "_" + key + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public PaymentType extractPaymentType(String merchantUid) {
        MerchnatUid merchantUidObj = parseMerchantUid(merchantUid);
        return merchantUidObj.getType();
    }

    public String extractKey(String merchantUid) {
        MerchnatUid merchantUidObj = parseMerchantUid(merchantUid);
        return merchantUidObj.getKey();
    }

    public MerchnatUid parseMerchantUid(String merchantUid) {
        if (merchantUid == null || merchantUid.isBlank()) {
            throw new IllegalArgumentException("merchantUid 가 null 또는 비어있습니다.");
        }
        String[] values = merchantUid.split("_");

        if (values.length != 3) throw new IllegalArgumentException("merchantUid가 적절한 형식이 아닙니다.");
        return new MerchnatUid(values[0], values[1], values[2]);
    }
}
