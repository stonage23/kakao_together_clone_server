package com.kakao.together.facade;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleDraftFundraisingResponse;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.domain.repository.FundraisingRepository;
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
    private final FundraisingRepository fundraisingRepository;

    /**
     * 모금 작성 이벤트 임시저장/저장 두 케이스에 대한 처리. 불러온 글을 다시 임시저장 하는 것은 Fundraising만 업데이트 할 뿐
     * @param request
     * @param draftStatus
     */
    // TODO [Refactor] 핸들러 쓰지말고 로직 나누기
    public void createFundraising(EditFundraisingRequest request, DraftStatus draftStatus) {

        FundraisingDraftHandler draftHandler = draftHandlers.stream()
                .filter(handler -> handler.supports(draftStatus, request.getFundraisingId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        draftHandler.handle(request);
    }

    @Transactional
    public void updateFundraising(EditFundraisingRequest request) {
        Fundraising fundraising = fundraisingRepository.findById(request.getFundraisingId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY));
        postService.updatePost(request, fundraising.getPost().getId());
        fundraisingService.updateFundraising(request.getFundraisingId(), request);
    }

    public void changeFundraisingStatus(Long fundraisingId, String status) {
        fundraisingService.updateFundraisingStatus(fundraisingId, status);
    }

    public List<SimpleDraftFundraisingResponse> getAllDraftFundraising() {
        return fundraisingService.findAllDraftFundraisings();
    }

    public EditFundraisingResponse getDraftFundraising(Long fundraisingId) {
        return fundraisingService.findDraftFundraising(fundraisingId);
    }

    public EditFundraisingResponse getFundraising(Long id) {
        return fundraisingService.findFundraising(id);
    }

    public void updateToPuplished(Long fundraisingId) {
        fundraisingService.updateDraftToPublished(fundraisingId);
    }

    public void deleteFundraising(Long id) {
        fundraisingService.deleteIfExists(id);
    }
}
