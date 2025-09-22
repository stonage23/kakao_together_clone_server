package com.kakao.together.external.paymentgate.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.together.external.paymentgate.helper.PortOneTokenProvider;
import com.kakao.together.external.paymentgate.service.PaymentGateClient;
import com.kakao.together.external.paymentgate.web.dto.CustomCancelData;
import com.kakao.together.external.paymentgate.web.dto.PaymentResponse;
import com.kakao.together.external.paymentgate.exception.PaymentGateResponseException;
import com.kakao.together.external.paymentgate.exception.PaymentGateTokenException;
import com.kakao.together.service.payment.internal.PaymentInternalService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class PortOneClient implements PaymentGateClient {

    private final IamportClient iamportClient;
    private final PortOneTokenProvider portOneTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(Logger.class);
    private static final String BASE_URL = IamportClient.API_URL;

    public PortOneClient(final PortOneTokenProvider portOneTokenProvider, @Value("${imp.api.key}") final String key, @Value("${imp.api.secret}") final String secret, final PaymentInternalService paymentInternalService) {
        this.portOneTokenProvider = portOneTokenProvider;
        this.iamportClient = new com.siot.IamportRestClient.IamportClient(key, secret);
    }

    @Override
    public PaymentResponse getPayment(String impUid) {

        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getToken())
                .build();

        IamportResponse iamportResponse = webClient.get()
                .uri("/payments/{impUid}", impUid)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.isError()
                        , clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("API Error - Status: {}, Body: {}", clientResponse.statusCode(), errorBody);
                                            return Mono.error(new PaymentGateResponseException("API call failed with status: " + clientResponse.statusCode()));
                                        })
                )
                .bodyToMono(IamportResponse.class)
                .block();

        PaymentResponse paymentResponse = objectMapper.convertValue(iamportResponse.getResponse(), PaymentResponse.class);

        if (paymentResponse == null || paymentResponse.getMerchantUid() == null) {
            throw new PaymentGateResponseException("PG 데이터 조회 실패 또는 merchantUid가 존재하지 않음; impUid: {}" + impUid);
        }
        return paymentResponse;
    }

    @Override
    public PaymentResponse refundPayment(String impUid) {

        CustomCancelData cancelData = new CustomCancelData(impUid, true);

        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getToken())
                .build();

        IamportResponse response = webClient.post()
                .uri("/payments/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cancelData)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.isError()
                        , clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("API Error - Status: {}, Body: {}", clientResponse.statusCode(), errorBody);
                                            return Mono.error(new PaymentGateResponseException("API call failed with status: " + clientResponse.statusCode()));
                                        })
                )
                .bodyToMono(IamportResponse.class)
                .doOnError(throwable -> log.error("에러임: " + throwable.getMessage(), throwable))
                .block();

        if (response.getResponse() == null) {
            throw new PaymentGateResponseException(response.getMessage());
        }

        PaymentResponse paymentResponse = objectMapper.convertValue(response.getResponse(), PaymentResponse.class);

        if (paymentResponse == null || paymentResponse.getMerchantUid() == null) {
            throw new PaymentGateResponseException("PG 데이터 조회 실패 또는 merchantUid가 존재하지 않음; impUid: " + impUid);
        }
        return paymentResponse;
    }

    private String getToken() throws PaymentGateTokenException {
        String token = portOneTokenProvider.getToken();
        if (token != null) return token;

        IamportResponse<AccessToken> iamportResponse = null;

        try {
            iamportResponse = iamportClient.getAuth();
        } catch (IamportResponseException | IOException e) {
            throw new PaymentGateTokenException("PG사 접근 토큰 발급 실패");
        }
        portOneTokenProvider.setToken(iamportResponse.getResponse().getToken());
        return iamportResponse.getResponse().getToken();
    }
}
