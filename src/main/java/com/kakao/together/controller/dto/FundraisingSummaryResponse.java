package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.fundraising.Fundraising;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
public class FundraisingSummaryResponse {
    private Long fundraisingId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetAmount;
    private String title;
    private Integer currentAmount;
    private AgencySummaryResponse agency;

    public static FundraisingSummaryResponse fromEntity(Fundraising fundraising) {
        return FundraisingSummaryResponse.builder()
                .fundraisingId(fundraising.getId())
                .startDate(fundraising.getStartDate())
                .endDate(fundraising.getEndDate())
                .targetAmount(fundraising.getTargetAmount())
                .title(fundraising.getTitle())
                .currentAmount(fundraising.getFundraisingStatus().getCurrentAmount())
                .agency(AgencySummaryResponse.fromEntity(fundraising.getAgency()))
                .build();
    }
}
