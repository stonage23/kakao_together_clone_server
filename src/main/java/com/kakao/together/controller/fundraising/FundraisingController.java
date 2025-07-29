package com.kakao.together.controller.fundraising;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.facade.FundraisingFacade;
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

    private final FundraisingFacade fundraisingFacade;

    @GetMapping("/api/fundraisings/{id}")
    public ResponseEntity<FundraisingResponse> getOngoingFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingFacade.getOngoingFundraising(id));
    }

    @GetMapping("/api/fundraisings/expiring-soon")
    public ResponseEntity<List<FundraisingResponse>> getExpiringSoonFundraising(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(fundraisingFacade.getExpiringSoonFundraisings(limit));
    }

    @GetMapping("/api/fundraisings/total-donations")
    public ResponseEntity<List<FundraisingResponse>> getTopFundraisings(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(fundraisingFacade.getTopFundraisings(limit));
    }
}
