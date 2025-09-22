package com.kakao.together.service.fundraising;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import org.springframework.transaction.annotation.Transactional;

public interface FundraisingDraftHandler {
    boolean supports(DraftStatus status, Long fundraisingId);
    @Transactional
    void handle(EditFundraisingRequest request);
}
