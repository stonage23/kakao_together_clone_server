package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.comment.dto.CommentDto.CommentRequest;
import com.kakao.together.controller.donation.dto.DonationDto.*;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.security.CustomUserDetails;
import com.kakao.together.service.comment.CommentService;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationCreationService {

    private final DonationService donationService;
    private final CommentService commentService;

    @Transactional
    // TODO FundraisingCurrent 업데이트 하는 로직 추가
    public void createCommentDonation(CustomUserDetails userDetails, DonationCreateWithCommentWrapper request) {
        CommentDonationRequest donationCreateRequest = request.getDonationRequest();
        CommentRequest commentCreateRequest = request.getCommentRequest();

        boolean isPresent = donationService.isPresentValidDonation(donationCreateRequest.getFundraisingId(), userDetails.getUserId(), DonationType.COMMENT);

        if (!isPresent) {
            donationService.createCommentDonation(userDetails.getUserId(), donationCreateRequest);
        }
        commentService.createComment(userDetails.getUserId(), commentCreateRequest);
    }

    @Transactional
    public DonationPendingResponse createDirectDonation(CustomUserDetails  userDetails, DonationPendingRequest request) {
        // TODO 테스트 위해서 유저 id 임으로 넣음
        Donation donation = donationService.createPendingDonation(1L, request);

        return DonationPendingResponse.builder()
                .donationId(donation.getId())
                .merchantUid(donation.getPaymentTransaction().getMerchantUid())
                .build();
    }
}
