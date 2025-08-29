package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.service.fundraising.FundraisingDraftHandler;
import com.kakao.together.service.fundraising.FundraisingService;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatedWithFundraisingHandler implements FundraisingDraftHandler {

    private final FundraisingService fundraisingService;
    private final PostService postService;

    @Override
    public boolean supports(DraftStatus status, Long fundraisingId) {
        return status == DraftStatus.PUBLISHED && fundraisingId != null;
    }

    @Override
    public void handle(Long fundraisingId, EditFundraisingRequest request) {
        postService.buildPost(request);
        fundraisingService.updateDraftToPublished(fundraisingId, request);
    }
}
