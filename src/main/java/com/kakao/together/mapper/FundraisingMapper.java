package com.kakao.together.mapper;

import com.kakao.together.controller.agency.dto.AgencyDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.domain.entity.fundraising.Fundraising;

public class FundraisingMapper {

    private FundraisingMapper() {}

    public static FundraisingResponse toFundraisingResponse(Fundraising fundraising, String thumbnailUrl) {
        StringBuilder summary = new StringBuilder();
        for (Content content : fundraising.getPost().getContents()) {

            if (summary.length() > 0) {
                summary.append(" ");
            }

            if (content instanceof TextContent textContent)
                summary.append((textContent.getText()));
            else if (content instanceof SubTitleContent subTitleContent)
                summary.append(subTitleContent.getSubtitle());

            if (summary.length() >= 200) {
                break;
            }
        }
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
                .directDonationCount(fundraising.getFundraisingCurrent().getDirectDonorCount())
                .indirectDonationCount(fundraising.getFundraisingCurrent().getIndirectDonorCount())
                .summary(summary.toString())
                .build();
    }
}
