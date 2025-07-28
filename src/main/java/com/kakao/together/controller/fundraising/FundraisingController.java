package com.kakao.together.controller.fundraising;

import com.kakao.together.annotation.Admin;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto.Save;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto.Update;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingDto.UpdateDraft;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleEditFundraisingResponse;
import com.kakao.together.exception.CustomException;
import com.kakao.together.facade.FundraisingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FundraisingController {

    private final FundraisingFacade fundraisingFacade;

    @Admin
    @PostMapping("/admin/fundraisings/temp")
    public ResponseEntity<Void> createTempFundraising(@RequestBody EditFundraisingDto fundraisingDto) {
        fundraisingFacade.createTempFundraising(fundraisingDto);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PutMapping("/admin/fundraisings/temp")
    public ResponseEntity<Void> updateTempFundraising(@RequestBody @Validated(UpdateDraft.class) EditFundraisingDto fundraisingDto) {
        if (fundraisingDto.getFundraisingId() == null) throw new CustomException("모금 게시글 임시저장 수정 실패: fundraisingId 누락");
        fundraisingFacade.updateTempFundraising(fundraisingDto);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PostMapping("/admin/fundraisings")
    public ResponseEntity<Void> createFundraising(@RequestBody @Validated(Save.class) EditFundraisingDto requestDto) {
        fundraisingFacade.createFundraising(requestDto);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PutMapping("/admin/fundraisings/{id}")
    public ResponseEntity<Void> updateFundraising(@PathVariable Long id, @RequestBody @Validated(Update.class) EditFundraisingDto requestDto) {
        fundraisingFacade.updateFundraising(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/fundraisings/{id}")
    public ResponseEntity<FundraisingResponse> getOngoingFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingFacade.getOngoingFundraising(id));
    }

    @GetMapping("/admin/temp/fundraisings")
    public ResponseEntity<List<SimpleEditFundraisingResponse>> getTempFundraisings() {
        return ResponseEntity.ok(fundraisingFacade.getTempFundraisings());
    }

    @GetMapping("/admin/temp/fundraisings/{id}")
    public ResponseEntity<EditFundraisingDto> getTempFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingFacade.findTemporaryFundraisingById(id));
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
