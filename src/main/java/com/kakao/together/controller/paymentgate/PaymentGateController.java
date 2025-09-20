package com.kakao.together.controller.paymentgate;

import com.kakao.together.controller.paymentgate.dto.PaymentGateDto.PortOneRequest;
import com.kakao.together.service.payment.PaymentVerificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentGateController {

    private final PaymentVerificationServiceImpl paymentVerificationHandlerImpl;

    @PostMapping("/validation")
    public ResponseEntity<Void> validatePayment(@RequestBody PortOneRequest request) {
        paymentVerificationHandlerImpl.verifyPayment(request.getImpUid());
        return ResponseEntity.ok().build();
    }
}
