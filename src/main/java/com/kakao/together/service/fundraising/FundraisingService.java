package com.kakao.together.service.fundraising;

import com.kakao.together.controller.comment.dto.CommentDto.CommentResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingPostResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;

import java.util.List;

public interface FundraisingService {

    Long createFundraising(EditFundraisingRequest request, Long postId);

    FundraisingResponse getOngoingFundraisingResponse(Long fundraisingId);

    Long updateFundraising(EditFundraisingRequest request);

    void updateFundraisingStatus(Long fundraisingId, String status);

    List<FundraisingResponse> findFundraisingsExpiringInThreeDaysLimit(int limit);

    List<FundraisingResponse> findFundraisingsTopLimit(int limit);

    void deleteIfExists(Long fundraisingId);

    EditFundraisingResponse findTempFundraisingById(Long id);

    List<FundraisingDto.SimpleTempFundraisingResponse> findAllTempFundraisings();

    void updateDraftToCreated(EditFundraisingRequest request);

    FundraisingPostResponse getFundraisingStory(Long fundraisingId);

    List<CommentResponse> findAllComments(Long fundraisingId);
}
