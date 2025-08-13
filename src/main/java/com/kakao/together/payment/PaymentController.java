package com.kakao.together.payment;

import com.kakao.together.controller.dto.PaymentDto.PaymentPendingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
