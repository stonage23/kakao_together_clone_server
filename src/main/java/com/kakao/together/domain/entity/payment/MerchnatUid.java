package com.kakao.together.domain.entity.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MerchnatUid {

    private PaymentType type;
    private String key;
    private String time;

    private MerchnatUid() {}

    @Builder
    public  MerchnatUid(String type, String key, String time) {
        this.type = switch (PaymentType.valueOf(type.toUpperCase())) {
            case DONATION -> PaymentType.DONATION;
        };
        this.key = key;
        this.time = time;
    }

    public String getKey() {
        if (this.key == null) return "";
        return this.key;
    }

    public String getTime() {
        if (this.time == null) return "";
        return this.time;
    }
}
