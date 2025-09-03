package com.kakao.together.controller.agency.dto;

import com.kakao.together.domain.entity.agency.Agency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AgencyCommand {

    public Agency toEntity() {
        return Agency.builder().build();
    }
}
