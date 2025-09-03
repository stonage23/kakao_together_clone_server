package com.kakao.together.controller.payment;

import com.kakao.together.controller.payment.dto.PaymentDto.PaymentPendingRequest;
import com.kakao.together.controller.payment.dto.PaymentDto.PaymentTransactionDetailResponse;
import com.kakao.together.service.payment.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private  final PaymentTransactionService paymentTransactionService;

    @PostMapping("")
    public ResponseEntity<Void> createPaymentAsPending(PaymentPendingRequest requestDto) {
        paymentTransactionService.savePaymentAsPending(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{merchantUid}/approval")
    public ResponseEntity<PaymentTransactionDetailResponse> getApprovalPaymentTransactionDetail(@PathVariable(value = "merchantUid") String merchantUid) {
        PaymentTransactionDetailResponse response = paymentTransactionService.findOnlyApprovalPaymentTransaction(merchantUid);
        return ResponseEntity.ok(response);
    }
}
