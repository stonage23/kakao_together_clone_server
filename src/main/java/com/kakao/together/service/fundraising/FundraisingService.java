package com.kakao.together.service.fundraising;

import com.kakao.together.controller.comment.dto.CommentDto.CommentResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.*;

import java.util.List;

public interface FundraisingService {

    Long createFundraising(EditFundraisingRequest request, Long postId);

    FundraisingResponse findOngoingFundraising(Long fundraisingId);

    Long updateFundraising(Long fundraisingId, EditFundraisingRequest request);

    void updateFundraisingStatus(Long fundraisingId, String status);

    List<FundraisingResponse> findFundraisingsExpiringInDays(int limit);

    List<FundraisingResponse> findFundraisingsTopLimit(int limit);

    List<FundraisingResponse> findFundraisingsOngoingRandom(int limit);

    void deleteIfExists(Long fundraisingId);

    EditFundraisingResponse findDraftFundraising(Long fundraisingId);

    List<SimpleDraftFundraisingResponse> findAllDraftFundraisings();

    void updateDraftToPublished(Long fundraisingId);

    FundraisingPostEditResponse findFundraisingStoryHtml(Long fundraisingId);

    List<CommentResponse> findAllComments(Long fundraisingId);

    EditFundraisingResponse findFundraising(Long fundraisingId);

    List<FundraisingResponse> findFundraisingsNearingGoal(int limit);

    FundraisingPostResponse findFundraisingStory(Long fundraisingId);
}
