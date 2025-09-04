package com.kakao.together.facade;

import com.kakao.together.controller.comment.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.donation.dto.DonationDto.*;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.comment.CommentService;
import com.kakao.together.service.donation.DonationService;
import com.kakao.together.service.paymentgate.impl.PortOnePaymentGateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationFacade {

    private final DonationService donationService;
    private final PortOnePaymentGateService portOnePaymentValidationService;
    private final CommentService commentService;

    @Transactional
    public void completeDonation(Long donationId, DonationCompleteRequest request) {
        portOnePaymentValidationService.verifyPayment(request.getImpUid());
        donationService.updateDonationToComplete(request.getMerchantUid(), donationId);
    }

    @Transactional
    public void cancelDonation(CustomUserDetails userDetails, Long donationId) {
        Donation donation = donationService.getDonationEntity(donationId);
        if (donation.getMember().getId() != userDetails.getId()) {
            log.warn("로그인 유저가 다른사람의 기부 내역을 취소하려는 시도가 있었습니다; memberId: {}, donationId: {}", donation.getMember().getId(), donationId);
            throw new CustomException(ErrorCode.FAILED_CANCEL_DONATION);
        }
        PaymentTransaction paymentTransaction = donation.getPaymentTransaction();
        if (paymentTransaction == null) {
            log.error("존재해야하는 결제 내역(PaymentTransaction)이 존재하지 않음; donationId: {}", donationId);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "기부의 결제 내역 조회 불가; donationId: " + donationId);
        }
        portOnePaymentValidationService.refundPayment(paymentTransaction.getImpUid());
        donationService.cancelDonationById(donationId);
    }

    @Transactional
    public void createCommentDonation(CustomUserDetails userDetails, DonationCreateWithCommentWrapper request) {
        CommentDonationRequest donationCreateRequest = request.getDonationRequest();
        CommentRequest commentCreateRequest = request.getCommentRequest();

        boolean isPresent = donationService.isPresentValidDonation(donationCreateRequest.getFundraisingId(), userDetails.getId(), DonationType.COMMENT);

        if (!isPresent) {
            donationService.createCommentDonation(userDetails. getId(), donationCreateRequest);
        }
        commentService.createComment(userDetails.getId(), commentCreateRequest);
    }

    public List<DonationsResponse> getAllMyDonations(CustomUserDetails userDetails) {
        return donationService.getAllDonationsForDonor(userDetails.getId());
    }

    public DonationPendingResponse createPendingDonation(CustomUserDetails  userDetails, DonationPendingRequest request) {
        return donationService.createPendingDonation(userDetails.getId(), request);
    }
}
