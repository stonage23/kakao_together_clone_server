package com.kakao.together.external.paymentgate.web;

import com.kakao.together.external.paymentgate.web.dto.PaymentGateDto.PortOneRequest;
import com.kakao.together.service.paymentgate.impl.PortOneVerificationService;
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

    private final PortOneVerificationService paymentVerificationHandlerImpl;

    @PostMapping("/validation")
    public ResponseEntity<Void> validatePayment(@RequestBody PortOneRequest request) {
        boolean success = paymentVerificationHandlerImpl.verifyPayment(request.getImpUid(), request.getMerchantUid());
        if (!success) return ResponseEntity.internalServerError().build();
        return ResponseEntity.ok().build();
    }
}
