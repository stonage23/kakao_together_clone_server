package com.kakao.together.service.fundraising;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.domain.entity.fundraising.DraftStatus;

public interface FundraisingDraftHandler {
    boolean supports(DraftStatus status, Long fundraisingId);
    void handle(Long fundraisingId, EditFundraisingRequest request);
}
