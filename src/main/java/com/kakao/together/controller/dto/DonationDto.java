package com.kakao.together.controller.dto;

import com.kakao.together.controller.dto.CommentDto.CommentRequest;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.payment.PaymentTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

public class DonationDto {

    private DonationDto () {}

    @Builder
    @AllArgsConstructor
    @Getter
    public static class DonationRequest {
        private String impUid;
        private String merchantUid;
        private BigDecimal amount;
        private Long fundraisingId;
        private String type;

        public Donation toEntity(Member donor, Fundraising fundraising, PaymentTransaction paymentTransaction) {
            DonationType donationType = switch (this.type) {
                case "DIRECT" -> DonationType.DIRECT;
                case "INDIRECT" -> DonationType.COMMENT;
                default -> throw new CustomException(ErrorCode.INVALID_ARGUMENT, "적절하지 않은 DonationType: " + this.type);
            };
            return Donation.builder()
                    .member(donor)
                    .fundraising(fundraising)
                    .status(DonationStatus.COMPLETE)
                    .paymentTransaction(paymentTransaction)
                    .type(donationType)
                    .build();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DonationCreateWithCommentWrapper {
        private CommentRequest commentRequest;
        private CommentDonationRequest donationRequest;
    }

    @AllArgsConstructor
    @Getter
    public static class CommentDonationRequest {
        private Long fundraisingId;
    }
}
