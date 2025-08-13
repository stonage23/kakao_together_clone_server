package com.kakao.together.paymentgate;

import com.kakao.together.paymentgate.service.PortOnePaymentValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{impUid}/refund")
    public ResponseEntity<Void> refundPayment(@PathVariable(value = "impUid") String impUid) {
        portOnePaymentValidationService.refundPayment(impUid);
        return ResponseEntity.ok().build();
    }
}
