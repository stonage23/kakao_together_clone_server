package com.kakao.together.external.paymentgate.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentGateDto {
    private PaymentGateDto() {}

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class PortOneRequest {
        @JsonProperty("imp_uid")
        private String impUid;
        @JsonProperty("merchant_uid")
        private String merchantUid;
    }
}
