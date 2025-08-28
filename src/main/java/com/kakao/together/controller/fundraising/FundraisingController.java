package com.kakao.together.controller.fundraising;

import com.kakao.together.controller.comment.dto.CommentDto;
import com.kakao.together.controller.comment.dto.CommentDto.CommentResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingPostResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FundraisingController {

    private final FundraisingService fundraisingService;

    @GetMapping("/api/fundraisings/{id}")
    public ResponseEntity<FundraisingResponse> getOngoingFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingService.getOngoingFundraisingResponse(id));
    }

    @GetMapping("/api/fundraisings/expiring-soon")
    public ResponseEntity<List<FundraisingResponse>> getExpiringSoonFundraising(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(fundraisingService.findFundraisingsExpiringInThreeDaysLimit(limit));
    }

    @GetMapping("/api/fundraisings/total-donations")
    public ResponseEntity<List<FundraisingResponse>> getTopFundraisings(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(fundraisingService.findFundraisingsTopLimit(limit));
    }

    @GetMapping("/api/fundraisings/{id}/post/story")
    public ResponseEntity<FundraisingPostResponse> getFundraisingStory(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingService.getFundraisingStory(id));
    }

    @GetMapping("/api/fundraisings/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable Long id) {
        return ResponseEntity.of(fundraisingService.findAllComments(id));
    }
}