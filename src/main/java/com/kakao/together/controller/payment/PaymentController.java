package com.kakao.together.controller.payment;

import com.kakao.together.service.payment.transaction.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentTransactionService paymentTransactionService;

//    @PostMapping("")
//    public ResponseEntity<Void> createPaymentAsPending(PaymentPendingRequest requestDto) {
//        paymentTransactionService.savePaymentAsPending(requestDto);
//        return ResponseEntity.ok().build();
//    }
}
