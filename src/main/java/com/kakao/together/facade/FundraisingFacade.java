package com.kakao.together.facade;

import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;

@Service
@RequiredArgsConstructor
public class FundraisingFacade {

    private final FundraisingService fundraisingService;

    public FundraisingResponse getOngoingFundraising(Long id) {
        return fundraisingService.getOngoingFundraisingResponse(id);
    }

    public List<FundraisingResponse> getExpiringSoonFundraisings(int limit) {
        return fundraisingService.findFundraisingsExpiringInThreeDaysLimit(limit);
    }

    public List<FundraisingResponse> getTopFundraisings(int limit) {
        return fundraisingService.findFundraisingsTopLimit(limit);
    }
}
