package com.kakao.together.controller.agency.dto;

import com.kakao.together.controller.image.dto.ImageCommand;
import com.kakao.together.domain.entity.agency.Agency;
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
    private ImageCommand logo;

    public static AgencySummaryResponse fromEntity(Agency agency) {
        return AgencySummaryResponse.builder()
                .agencyId(agency.getId())
                .name(agency.getName())
                .organizationType(agency.getOrganizationType())
                .logo(ImageCommand.fromEntity(agency.getLogo()))
                .build();
    }
}
