package com.kakao.together.service.agency.impl;

import com.kakao.together.domain.entity.fundraising.Agency;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.repository.AgencyRepository;
import com.kakao.together.service.agency.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final AgencyRepository agencyRepository;
    @Override
    public Agency getAgencyEntityById(Long agencyId) {
        return agencyRepository.findById(agencyId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 id에 해당하는 Agency 엔티티가 존재하지 않습니다.")
        );
    }
}
