package com.kakao.together.controller.fundraising.dto;

import com.kakao.together.controller.agency.dto.AgencySummaryResponse;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class FundraisingSummaryResponse {
    private Long fundraisingId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
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
                .currentAmount(fundraising.getFundraisingCurrent().getCurrentAmount())
                .agency(AgencySummaryResponse.fromEntity(fundraising.getAgency()))
                .build();
    }
}
