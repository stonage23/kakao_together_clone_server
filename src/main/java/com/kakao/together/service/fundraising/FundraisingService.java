package com.kakao.together.service.fundraising;

import com.kakao.together.controller.fundraising.dto.FundraisingDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.domain.entity.fundraising.Fundraising;

import java.util.Optional;

public interface FundraisingService {

    FundraisingResponse findFundraising(Long id);

    Fundraising createFundraising(FundraisingDto.EditFundraisingDto requestDto);

    Optional<Fundraising> getFundraisingEntity(Long fundraisingId);

    Fundraising createTempFundraising(FundraisingDto.EditFundraisingDto requestDto);

    Fundraising transTempToUpload(FundraisingDto.EditFundraisingDto requestDto);
}
