package com.kakao.together.facade;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleEditFundraisingResponse;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.service.fundraising.FundraisingDraftHandler;
import com.kakao.together.service.fundraising.FundraisingService;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundraisingAdminFacade {

    private final FundraisingService fundraisingService;
    private final PostService postService;
    private final List<FundraisingDraftHandler> draftHandlers;

    /**
     * 모금 작성 이벤트 임시저장/저장 두 케이스에 대한 처리. 불러온 글을 다시 임시저장 하는 것은 Fundraising만 업데이트 할 뿐
     * @param request
     * @param draftStatus
     */
    @Transactional
    public void createFundraising(EditFundraisingRequest request, DraftStatus draftStatus) {

        FundraisingDraftHandler draftHandler = draftHandlers.stream()
                .filter(handler -> handler.supports(draftStatus, request.getFundraisingId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PERMITTED_CONDITION));

        draftHandler.handle(request);
    }

    @Transactional
    public void updateFundraising(EditFundraisingRequest request) {

        postService.buildPost(request);
        fundraisingService.updateFundraising(request);
    }

    public void changeFundraisingStatus(Long fundraisingId, String status) {
        fundraisingService.updateFundraisingStatus(fundraisingId, status);
    }

    public List<SimpleEditFundraisingResponse> getAllTempFundraisings() {
        return fundraisingService.findAllTempFundraisings();
    }

    public EditFundraisingResponse getTempFundraising(Long fundraisingId) {
        Fundraising fundraising = fundraisingService.findTempFundraisingById(fundraisingId);
        String buildedHtml = postService.postToHtml(fundraising.getPost());
        return EditFundraisingResponse.fromEntity(fundraising, buildedHtml);
    }
}
