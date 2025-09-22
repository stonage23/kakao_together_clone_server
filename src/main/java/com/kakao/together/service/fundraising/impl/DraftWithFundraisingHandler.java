package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.repository.FundraisingRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.fundraising.FundraisingDraftHandler;
import com.kakao.together.service.fundraising.FundraisingService;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DraftWithFundraisingHandler implements FundraisingDraftHandler {

    private final FundraisingService fundraisingService;
    private final FundraisingRepository fundraisingRepository;
    private final PostService postService;

    @Override
    public boolean supports(DraftStatus status, Long fundraisingId) {
        return status == DraftStatus.DRAFT && fundraisingId != null;
    }

    @Override
    @Transactional
    public void handle(EditFundraisingRequest request) {
        Fundraising fundraising = fundraisingRepository.findById(request.getFundraisingId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY));
        postService.updatePost(request, fundraising.getPost().getId());
        fundraisingService.updateFundraising(request.getFundraisingId(), request);
    }
}
