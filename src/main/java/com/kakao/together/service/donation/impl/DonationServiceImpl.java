package com.kakao.together.service.donation.impl;

import com.kakao.together.controller.dto.DonationDto.DonationRequest;
import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.payment.PaymentTransaction;
import com.kakao.together.repository.DonationRepository;
import com.kakao.together.repository.FundraisingRepository;
import com.kakao.together.repository.MemberRepository;
import com.kakao.together.repository.PaymentRepository;
import com.kakao.together.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationServiceImpl implements DonationService {

    private final MemberRepository memberRepository;
    private final FundraisingRepository fundraisingRepository;
    private final PaymentRepository paymentRepository;
    private final DonationRepository donationRepository;

    @Override
    @Transactional
    public void createDonation(DonationRequest request, Long donorId) {

        Member doner = memberRepository.findById(donorId).orElseThrow(
                () -> {
                    log.error("전달 받은 donorId와 일치하는 유저 정보 없음; donorId: {}", donorId);
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "기부자 정보 DB에서 조회 실패");
                }
        );
        Fundraising fundraising = fundraisingRepository.findById(request.getFundraisingId()).orElseThrow(
                () -> {
                    log.error("전달 받은 fundraisingId와 일치하는 모금 정보 없음; fundraisingId: {}", request.getFundraisingId());
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "모금 정보 DB에서 조회 실패");
                }
        );
        PaymentTransaction paymentTransaction = paymentRepository.findByMerchantUid(request.getMerchantUid()).orElseThrow(
                () -> {
                    log.error("전달 받은 merchantUid와 일치하는 결제 내역 없음; merchantUid: {}", request.getMerchantUid());
                    return new CustomException(ErrorCode.NOT_FOUND_ENTITY, "결제 내역 DB에서 조회 실패");
                }
        );

        Donation donation = request.toEntity(doner, fundraising, paymentTransaction);

        donationRepository.save(donation);
    }

    @Override
    public Donation getDonationEntity(Long donationId) {
        return donationRepository.findById(donationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; donationId: " + donationId)
        );
    }

    @Override
    public void cancelDonationById(Long donationId) {
        Donation donation = donationRepository.findById(donationId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; donationId: " + donationId)
        );
        donation.updateStatus(DonationStatus.CANCELLED);
    }
}
