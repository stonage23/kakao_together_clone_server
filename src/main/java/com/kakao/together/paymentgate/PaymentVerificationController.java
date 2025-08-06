package com.kakao.together.paymentgate;

import com.kakao.together.paymentgate.service.PortOnePaymentValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentVerificationController {

    private final PortOnePaymentValidationService portOnePaymentValidationService;

    @PostMapping("/validation")
    public ResponseEntity<Void> validatePayment(@RequestParam(value = "impUid") String impUid) {
        portOnePaymentValidationService.verifyPayment(impUid);
        return ResponseEntity.ok().build();
    }
}
