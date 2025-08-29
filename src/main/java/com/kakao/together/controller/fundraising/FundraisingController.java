package com.kakao.together.controller.fundraising;

import com.kakao.together.controller.comment.dto.CommentDto.CommentResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingPostResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fundraisings")
public class FundraisingController {

    private final FundraisingService fundraisingService;

    @GetMapping("/{id}")
    public ResponseEntity<FundraisingResponse> getOngoingFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingService.findOngoingFundraising(id));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<FundraisingResponse>> getExpiringSoonFundraising(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(fundraisingService.findFundraisingsExpiringInDays(limit));
    }

    @GetMapping("/total-donations")
    public ResponseEntity<List<FundraisingResponse>> getTopFundraising(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(fundraisingService.findFundraisingsTopLimit(limit));
    }

    @GetMapping("/{id}/story")
    public ResponseEntity<FundraisingPostResponse> getFundraisingStory(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingService.findFundraisingStory(id));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getAllComments(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingService.findAllComments(id));
    }
}