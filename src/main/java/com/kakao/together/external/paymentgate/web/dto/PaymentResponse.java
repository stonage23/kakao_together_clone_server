package com.kakao.together.external.paymentgate.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    @JsonProperty("merchant_uid")
    private String merchantUid;
    @JsonProperty("imp_uid")
    private String impUid;
    private BigDecimal amount;
    private String currency;
    private String status;
    @JsonProperty("pg_provider")
    private String pgProvider;
    @JsonProperty("started_at")
    private Long startedAt;
    @JsonProperty("paid_at")
    private Long paidAt;
    @JsonProperty("failed_at")
    private Long failedAt;
    @JsonProperty("cancelled_at")
    private Long cancelledAt;
    @JsonProperty("fail_reason")
    private String failReason;
    private String name;
    @JsonProperty("pay_method")
    private String payMethod;
    @JsonProperty("emb_pg_provider")
    private String embPgProvider;
}
