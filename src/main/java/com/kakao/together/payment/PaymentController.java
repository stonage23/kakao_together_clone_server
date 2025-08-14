package com.kakao.together.payment;

import com.kakao.together.controller.dto.PaymentDto.PaymentPendingDto;
import com.kakao.together.controller.dto.PaymentDto.PaymentTransactionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private  final PaymentService paymentService;

    @PostMapping("")
    public ResponseEntity<Void> createPaymentAsPending(PaymentPendingDto requestDto) {
        paymentService.savePaymentAsPending(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{merchantUid}/approval")
    public ResponseEntity<PaymentTransactionDetailResponse> getApprovalPaymentTransactionDetail(@PathVariable(value = "merchantUid") String merchantUid) {
        PaymentTransactionDetailResponse response = paymentService.findOnlyApprovalPaymentTransaction(merchantUid);
        return ResponseEntity.ok(response);
    }
}
