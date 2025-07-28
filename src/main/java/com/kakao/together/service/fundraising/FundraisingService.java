package com.kakao.together.service.fundraising;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.domain.entity.fundraising.Fundraising;

import java.util.List;
import java.util.Optional;

public interface FundraisingService {

    Fundraising createTempFundraising(Fundraising fundrasing);

    Fundraising createFundraising(Fundraising fundraising);

    FundraisingResponse getOngoingFundraisingResponse(Long fundraisingId);

    Optional<Fundraising> findFundraisingNullable(Long fundraisingId);

    Fundraising findFundraisingNullCheck(Long fundraisingId);

    List<FundraisingResponse> findFundraisingsExpiringInThreeDaysLimit(int limit);

    List<FundraisingResponse> findFundraisingsTopLimit(int limit);

    void deleteIfExists(Long fundraisingId);

    Fundraising findTempFundraisingById(Long id);

    List<Fundraising> findAllTempFundraisings();
}
