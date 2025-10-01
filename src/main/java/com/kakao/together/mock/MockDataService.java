package com.kakao.together.mock;

import com.kakao.together.domain.entity.donation.Donation;
import com.kakao.together.domain.entity.donation.DonationStatus;
import com.kakao.together.domain.entity.donation.DonationType;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.entity.fundraising.FundraisingStatus;
import com.kakao.together.domain.entity.member.Member;
import com.kakao.together.domain.repository.DonationRepository;
import com.kakao.together.domain.repository.FundraisingRepository;
import com.kakao.together.domain.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("mock")
public class MockDataService {

    private final FundraisingRepository fundraisingRepository;
    private final DonationRepository donationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void generateMockData() {

        Faker faker = new Faker(new Locale("ko"));

        List<Fundraising> fundraisings = new ArrayList<>();
        List<Donation> donations = new ArrayList<>();

        log.info("[INFO] DataFaker를 사용하여 Mock 데이터 생성을 시작합니다...");

        // 1. 모금(Fundraising) 데이터 50개 생성
        for (int i = 0; i < 100; i++) {
            LocalDateTime createdAt = faker.date().past(365, TimeUnit.DAYS).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            Date startDate_f = Date.from(LocalDateTime.now().minusDays(3).atZone(ZoneId.systemDefault()).toInstant());
            Date startDate_t = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
            Date endDate_f = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            Date endDate_t = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());

            Fundraising fundraising = Fundraising.builder()
                    .title(faker.lorem().sentence(20))
                    .targetAmount(faker.number().numberBetween(10000000, 100000000))
                    .fundraisingStatus(FundraisingStatus.ONGOING)
                    .draftStatus(DraftStatus.PUBLISHED)
                    .startDate(faker.date().between(startDate_f, startDate_t).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .endDate(faker.date().between(endDate_f, endDate_t).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();

            fundraisings.add(fundraising);

            Fundraising createdFundraising = fundraisingRepository.save(fundraising);
            Member member = memberRepository.findById(1L).get();

            // 2. 각 모금당 0~30개의 기부(Donation) 데이터 생성
            int donationCount = faker.number().numberBetween(0, 1000);
            long totalDonationAmount = 0;

            Date updated_f = Date.from(LocalDateTime.now().minusDays(5).atZone(ZoneId.systemDefault()).toInstant());
            Date updated_t = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

            for (int j = 0; j < donationCount; j++) {
                long donationAmount = faker.number().numberBetween(1000, 10000L);
                totalDonationAmount += donationAmount;

                Donation donation = Donation.builder()
                        .fundraising(createdFundraising)
                        .amount(donationAmount)
                        .member(member)
                        .status(faker.options().option(DonationStatus.class))
                        .completedAt(faker.date().between(updated_f, updated_t).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .type(faker.options().option(DonationType.class))
                        .build();
                Donation createdDonation = donationRepository.save(donation);
            }
        }

        // 3. 생성된 데이터를 DB에 한번에 저장
        donationRepository.saveAll(donations);

        log.info("[INFO] Mock 데이터 생성 및 저장이 완료되었습니다.");
    }
}
