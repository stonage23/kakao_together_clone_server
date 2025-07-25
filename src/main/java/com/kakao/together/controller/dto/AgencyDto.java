package com.kakao.together.controller.dto;

import com.kakao.together.domain.entity.fundraising.Agency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AgencyDto {

    private Long id;
    private String name;
    private String parentCompanyName;
    private String buisinessNumber;
    private String address;
    private String organizationType;

    public static AgencyDto fromEntity(Agency agency) {
        return AgencyDto.builder()
                .id(agency.getId())
                .name(agency.getName())
                .parentCompanyName(agency.getParentCompanyName())
                .buisinessNumber(agency.getBuisinessNumber())
                .address(agency.getAddress())
                .organizationType(agency.getOrganizationType())
                .build();
    }
}
