package com.kakao.together.service.donation.impl;

import com.kakao.together.controller.donation.dto.DonationDto.CommentDonationRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationPendingRequest;
import com.kakao.together.controller.donation.dto.DonationDto.DonationsResponse;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.entity.payment.PaymentTransaction;
import com.kakao.together.domain.entity.payment.PaymentType;
import com.kakao.together.domain.repository.DonationRepository;
import com.kakao.together.domain.repository.FundraisingRepository;
import com.kakao.together.domain.repository.MemberRepository;
import com.kakao.together.domain.repository.PaymentTransactionRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.donation.DonationService;
import com.kakao.together.service.member.MemberService;
import com.kakao.together.service.payment.helper.MerchantUidProviderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationServiceImpl implements DonationService {

    private final MemberRepository memberRepository;
    private final FundraisingRepository fundraisingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final DonationRepository donationRepository;
    private final MerchantUidProviderImpl merchantUidProviderImpl;
    private final MemberService memberService;

    @Value("${together.donation.comment.amount}")
    private Long commentDonationAmount;

    @Override
    @Transactional
    public Donation completeDonation(Long donationId) {

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DONATION));

        donation.completeDonation();
        return donation;
    }

    @Override
    public void cancelDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; donationId: " + donationId)
        );
        donation.cancelDonation();
    }

    @Override
    public boolean isPresentValidDonation(Long fundraisingId, Long donorId, DonationType type) {
        return donationRepository.existsByMemberIdAndFundraisingIdAndStatusAndType(donorId, fundraisingId, DonationStatus.COMPLETE, type);
    }

    @Override
    @Transactional
    public void createCommentDonation(Long donorId, CommentDonationRequest donationCreateRequest) {
        Fundraising fundraising = fundraisingRepository.findById(donationCreateRequest.getFundraisingId()).orElseThrow(
                () -> {
                    log.error("전달 받은 fundraisingId와 일치하는 모금 정보 없음; fundraisingId: {}", donationCreateRequest.getFundraisingId());
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "모금 정보 DB에서 조회 실패");
                }
        );

        Member doner = memberRepository.findById(donorId).orElseThrow(
                () -> {
                    log.error("전달 받은 donorId와 일치하는 유저 정보 없음; donorId: {}", donorId);
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "기부자 정보 DB에서 조회 실패");
                }
        );

        Donation donation = Donation.builder()
                .fundraising(fundraising)
                .status(DonationStatus.COMPLETE)
                .type(DonationType.COMMENT)
                .member(doner)
                .amount(commentDonationAmount)
                .build();
        donationRepository.save(donation);
    }

    @Override
    public List<DonationsResponse> getAllDonationsForDonor(Long donorId) {
        List<Donation> donations = donationRepository.findAllByMemberId(donorId);
        return donations.stream().map(DonationsResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Donation createPendingDonation(Long donorId, DonationPendingRequest request) {
        Member donor = memberRepository.findById(donorId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "로그인한 유저의 memberId 조회 실패; memberId: " + donorId));
        Fundraising fundraising = fundraisingRepository.findById(request.getFundraisingId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FUNDRAISING));

        Donation donation = Donation.builder()
                .member(donor)
                .status(DonationStatus.PENDING)
                .fundraising(fundraising)
                .type(DonationType.DIRECT)
                .amount(request.getAmount())
                .build();

        Donation createdDonation = donationRepository.save(donation);

        String merchantUid = merchantUidProviderImpl.generateMerchantUid(PaymentType.DONATION, String.valueOf(createdDonation.getId()));

        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
                .amount(request.getAmount())
                .merchantUid(merchantUid)
                .build();

        createdDonation.linkPaymentTransaction(paymentTransaction);

        return createdDonation;
    }

    @Override
    public void cancelPendingDonation(Long donorId, Long donationId) {

        Member donor;

        try {
            donor = memberService.getMember(donorId);
        } catch (NoSuchElementException e) {
            log.error("기부 내역 취소 실패: 존재하지 않는 유저; memberId: {}", donorId);
            throw new CustomException(ErrorCode.FAILED_CANCEL_DONATION);
        }

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> {
                    log.error("기부 내역 취소 실패: 존재하지 않는 기부내역; donationId: {}", donationId);
                    return new CustomException(ErrorCode.FAILED_CANCEL_DONATION);
                });

        if (!donor.equals(donation.getMember())) {
            log.error("기부 내역 취소요청 실패: 기부자와 현재 로그인한 유저의 id가 다름; loginId={}; donorId={}", donorId, donationId);
            throw new CustomException(ErrorCode.FAILED_CANCEL_DONATION);
        }

        donation.requestDonationCancel();
    }

    @Override
    public Donation getDonation(Long donationId, Long donorId) {
        return donationRepository.findByIdAndMemberId(donationId, donorId)
                .orElseThrow(() -> new NoSuchElementException("not present donation; donationId=" + donationId + "; memberId=" + donorId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failCancelDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new NoSuchElementException("not present donation; donationId=" + donationId));
        donation.failCancelDonation();
    }

    @Override
    public Donation getDonation(Long donationId) {
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new NoSuchElementException("not present donation; donationId=" + donationId));
    }

    @Override
    public Donation failDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new NoSuchElementException("not present donation; donationId=" + donationId));
        donation.failDonation();
        return donation;
    }

    @Override
    public void forceCompleteDonation(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DONATION));

        try {
            donation.forceCompleteDonation();
        } catch (IllegalStateException e) {
            log.warn("관리자가 complete강제한 donation의 상태가 불완전함.", e);
        }
    }

    @Override
    public void cancelByMember(Long donationId) {
        Donation donation = donationRepository.findById(donationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; donationId: " + donationId)
        );
        donation.cancelByUser();
    }
}
