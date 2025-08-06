package com.kakao.together.paymentgate.service;

import com.kakao.together.paymentgate.PaymentDetails;
import com.kakao.together.paymentgate.PortOnePaymentResponse;
import com.kakao.together.paymentgate.cache.PortOneTokenProvider;
import com.kakao.together.paymentgate.exception.PaymentCancelException;
import com.kakao.together.paymentgate.exception.PaymentGateException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
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

@Service
public class PortOnePaymentValidationService {

    private final PortOneTokenProvider portOneTokenProvider;
    private final PaymentDetailsService paymentDetailsService;
    private final IamportClient iamportClient;

    private static final String BASE_URL = IamportClient.API_URL;
    private static final Logger log = LoggerFactory.getLogger(PortOnePaymentValidationService.class);

    public PortOnePaymentValidationService(final PortOneTokenProvider portOneTokenProvider, @Value("${imp.api.key}") final String key, @Value("${imp.api.secret}") final String secret, final PaymentDetailsService paymentDetailsService) {
        this.portOneTokenProvider = portOneTokenProvider;
        this.iamportClient = new IamportClient(key, secret);
        this.paymentDetailsService = paymentDetailsService;
    }

    private String getToken() {
        try {
            String token = portOneTokenProvider.getToken();
            if (token != null) return token;

            IamportResponse<AccessToken> iamportResponse = iamportClient.getAuth();
            portOneTokenProvider.setToken(iamportResponse.getResponse().getToken());
            return iamportResponse.getResponse().getToken();
        } catch (IamportResponseException e) {
            throw new PaymentGateException("PortOne API 요청에 필요한 토큰 생성 실패", HttpStatus.UNAUTHORIZED, e);
        } catch (IOException e) {
            throw new PaymentGateException("PortOne API 요청에 필요한 토큰 생성 실패", HttpStatus.UNAUTHORIZED, e);
        }
    }

    public void verifyPayment(String impUid) {

        PortOnePaymentResponse response = getPaymentDetails(impUid);

        PaymentDetails paymentDetails = paymentDetailsService.loadPaymentByMerchantUid(response.getGetMerchantUid());

        if (!response.getPrice().equals(paymentDetails.getAmount())) {
            refundPayment(impUid);
            return;
        }

        String merchantUid = paymentDetails.getMerchantUid();
        if (merchantUid == null) {
            log.error("결제 검증은 완료되었지만 다른 이유로 검증 완료 처리 불가; merchantUid 가 null; impUid: {}", impUid);
            refundPayment(impUid);
        }

        paymentDetailsService.updatePaymentApproval(paymentDetails.getMerchantUid());
    }

    private PortOnePaymentResponse getPaymentDetails(String impUid) {
        WebClient webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.getToken())
                .build();

        return webClient.get()
                .uri("/payments/{impUid}", impUid)
                .retrieve()
                .bodyToMono(PortOnePaymentResponse.class)
                .block();
    }

    // TODO 예외처리 리팩토링

    /**
     * PG사 결제 취소가 되면 200응답처리. 서버 내부 문제는 로그로 남기기
     *
     * @param impUid
     */
    public void refundPayment(String impUid) {
        String merchantUid = this.cancelPayment(impUid).getMerchantUid();
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
    private PaymentDetails cancelPayment(String impUid) {

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
            throw new PaymentCancelException("결제 취소에 실패하였습니다. 서버 관리자에게 문의해주세요", HttpStatus.INTERNAL_SERVER_ERROR);

        return response.getResponse();
    }
}
