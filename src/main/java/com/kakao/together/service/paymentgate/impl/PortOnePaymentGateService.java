package com.kakao.together.service.paymentgate.impl;

import com.kakao.together.api.paymentgate.exception.PaymentGateException;
import com.kakao.together.api.paymentgate.paymentDetails.PaymentDetails;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.mapper.PaymentResponseMapper;
import com.kakao.together.service.cache.PortOneTokenProvider;
import com.kakao.together.service.paymentgate.PaymentDetailsService;
import com.kakao.together.service.paymentgate.PaymentGateService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Objects;

@Service
public class PortOnePaymentGateService implements PaymentGateService {

    private final PortOneTokenProvider portOneTokenProvider;
    private final PaymentDetailsService paymentDetailsService;
    private final PaymentResponseMapper<Payment> iamportPaymentMapper;
    private final IamportClient iamportClient;

    private static final String BASE_URL = IamportClient.API_URL;
    private static final Logger log = LoggerFactory.getLogger(PortOnePaymentGateService.class);

    public PortOnePaymentGateService(final PortOneTokenProvider portOneTokenProvider, @Value("${imp.api.key}") final String key, @Value("${imp.api.secret}") final String secret, final PaymentDetailsService paymentDetailsService, PaymentResponseMapper<Payment> iamportPaymentMapper) {
        this.portOneTokenProvider = portOneTokenProvider;
        this.iamportPaymentMapper = iamportPaymentMapper;
        this.iamportClient = new IamportClient(key, secret);
        this.paymentDetailsService = paymentDetailsService;
    }

    private String getToken() throws IamportResponseException, IOException {
        String token = portOneTokenProvider.getToken();
        if (token != null) return token;

        IamportResponse<AccessToken> iamportResponse = iamportClient.getAuth();
        portOneTokenProvider.setToken(iamportResponse.getResponse().getToken());
        return iamportResponse.getResponse().getToken();
    }

    @Override
    public void verifyPayment(String impUid) {

        Payment pgPayment = null;
        try {
            pgPayment = getPaymentDetails(impUid);
        } catch (IamportResponseException | IOException e) {
            refundPayment(impUid);
            throw new CustomException(e, "PortOne API 요청에 필요한 토큰 생성 실패");
        }

        if (pgPayment == null || pgPayment.getMerchantUid() == null) {
            refundPayment(impUid);
            throw new CustomException(ErrorCode.FAILED_VERIFY_PAYMENT, "PG 데이터 조회 실패 또는 merchantUid가 존재하지 않음; impUid: {}" + impUid);
        }

        PaymentDetails paymentDetails;
        try {
            String merchantUid = pgPayment.getMerchantUid();
            paymentDetails = paymentDetailsService.loadPaymentByMerchantUid(merchantUid);
        } catch (CustomException e) {
            refundPayment(impUid);
            throw e;
        }

        if (!Objects.equals(pgPayment.getAmount(), paymentDetails.getAmount())) {
            refundPayment(impUid);
            throw new CustomException(ErrorCode.FAILED_VERIFY_PAYMENT, "결제 금액이 일치하지 않음; impUid: " + impUid + ", merchantUid: " + paymentDetails.getMerchantUid());
        }

        paymentDetailsService.completePayment(iamportPaymentMapper.toPaymentResponse(pgPayment));
    }

    private Payment getPaymentDetails(String impUid) throws IamportResponseException, IOException {

        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getToken())
                .build();

        return webClient.get()
                .uri("/payments/{impUid}", impUid)
                .retrieve()
                .bodyToMono(Payment.class)
                .block();
    }

    // TODO 예외처리 리팩토링

    /**
     * PG사 결제 취소가 되면 200응답처리. 서버 내부 문제는 로그로 남기기
     *
     * @param impUid
     */
    @Override
    public void refundPayment(String impUid) {
        String merchantUid = null;
        try {
            merchantUid = this.cancelPayment(impUid).getMerchantUid();
        } catch (IamportResponseException | IOException e) {
            throw new CustomException(ErrorCode.FAILED_PAYMENT_CANCEL);
        }
        if (merchantUid == null) {
            log.error("결제건에 대한 취소 및 환불 처리는 완료되었으나 서버 DB 처리 불가; merchantUid 가 null; impUid: {}", impUid);
        } else {
            paymentDetailsService.updatePaymentCancellation(merchantUid);
        }
    }

    /**
     * cancelPayment을 호출하는 메소드에서 트랜젝션 관리
     *
     * @param impUid
     * @return
     */
    private PaymentDetails cancelPayment(String impUid) throws IamportResponseException, IOException {

        CancelData cancelData = new CancelData(impUid, true);

        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getToken())
                .build();

        IamportResponse<PaymentDetails> response = webClient.post()
                .uri("/payments/cancel")
                .bodyValue(cancelData)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new PaymentGateException("결제 취소 실패", HttpStatus.BAD_REQUEST)))
                )
                .bodyToMono(new ParameterizedTypeReference<IamportResponse<PaymentDetails>>() {
                })
                .block();

        if (response.getResponse() == null)
            throw new CustomException(ErrorCode.FAILED_PAYMENT_CANCEL, "PG사 결제 취소 요청에 대한 응답이 null");

        return response.getResponse();
    }
}
