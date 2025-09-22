package com.kakao.together.mapper;

import com.kakao.together.controller.agency.dto.AgencyDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.domain.entity.fundraising.Fundraising;

public class FundraisingMapper {

    private FundraisingMapper() {}

    public static FundraisingResponse toFundraisingResponse(Fundraising fundraising, String thumbnailUrl) {
        return FundraisingResponse.builder()
                .id(fundraising.getId())
                .title(fundraising.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .targetAmount(fundraising.getTargetAmount())
                .startDate(fundraising.getStartDate())
                .endDate(fundraising.getEndDate())
                .agency(AgencyDto.fromEntity(fundraising.getAgency()))
                .currentAmount(fundraising.getFundraisingCurrent().getCurrentAmount())
                .directDonationAmount(fundraising.getFundraisingCurrent().getDirectDonationAmount())
                .indirectDonationAmount(fundraising.getFundraisingCurrent().getIndirectDonationAmount())
                .directDonationAmount(fundraising.getFundraisingCurrent().getDirectDonationAmount())
                .indirectDonationAmount(fundraising.getFundraisingCurrent().getIndirectDonationAmount())
                .directDonorCount(fundraising.getFundraisingCurrent().getDirectDonorCount())
                .indirectDonorCount(fundraising.getFundraisingCurrent().getIndirectDonorCount())
                .build();
    }
}
