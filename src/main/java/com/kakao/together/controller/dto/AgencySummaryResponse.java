package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.fundraising.Agency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AgencySummaryResponse {
    private Long agencyId;
    private String name;
    private String organizationType;
    private ImageDto logo;

    public static AgencySummaryResponse fromEntity(Agency agency) {
        return AgencySummaryResponse.builder()
                .agencyId(agency.getId())
                .name(agency.getName())
                .organizationType(agency.getOrganizationType())
                .logo(ImageDto.fromEntity(agency.getLogo()))
                .build();
    }
}
