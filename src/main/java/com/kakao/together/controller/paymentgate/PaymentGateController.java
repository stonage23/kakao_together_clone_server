package com.kakao.together.controller.paymentgate;

import com.kakao.together.service.paymentgate.PaymentGateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentGateController {

    private final PaymentGateService paymentGateService;

    @PostMapping("/{impUid}/validation")
    public ResponseEntity<Void> validatePayment(@PathVariable String impUid) {
        paymentGateService.verifyPayment(impUid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{impUid}/refund")
    public ResponseEntity<Void> refundPayment(@PathVariable String impUid) {
        paymentGateService.refundPayment(impUid);
        return ResponseEntity.ok().build();
    }
}
