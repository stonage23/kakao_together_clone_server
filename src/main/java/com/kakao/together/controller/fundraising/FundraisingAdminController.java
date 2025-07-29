package com.kakao.together.controller.fundraising;

import com.kakao.together.annotation.Admin;
import com.kakao.together.controller.fundraising.dto.FundraisingDto;
import com.kakao.together.exception.CustomException;
import com.kakao.together.facade.FundraisingAdminFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FundraisingAdminController {

    private final FundraisingAdminFacade fundraisingAdminFacade;

    @Admin
    @PostMapping("/admin/fundraisings/temp")
    public ResponseEntity<Void> createTempFundraising(@RequestBody FundraisingDto.EditFundraisingDto fundraisingDto) {
        fundraisingAdminFacade.createTempFundraising(fundraisingDto);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PutMapping("/admin/fundraisings/temp")
    public ResponseEntity<Void> updateTempFundraising(@RequestBody @Validated(FundraisingDto.EditFundraisingDto.UpdateDraft.class) FundraisingDto.EditFundraisingDto fundraisingDto) {
        if (fundraisingDto.getFundraisingId() == null) throw new CustomException("모금 게시글 임시저장 수정 실패: fundraisingId 누락");
        fundraisingAdminFacade.updateTempFundraising(fundraisingDto);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PostMapping("/admin/fundraisings")
    public ResponseEntity<Void> createFundraising(@RequestBody @Validated(FundraisingDto.EditFundraisingDto.Save.class) FundraisingDto.EditFundraisingDto requestDto) {
        fundraisingAdminFacade.createFundraising(requestDto);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PutMapping("/admin/fundraisings/{id}")
    public ResponseEntity<Void> updateFundraising(@PathVariable Long id, @RequestBody @Validated(FundraisingDto.EditFundraisingDto.Update.class) FundraisingDto.EditFundraisingDto requestDto) {
        fundraisingAdminFacade.updateFundraising(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/temp/fundraisings")
    public ResponseEntity<List<FundraisingDto.SimpleEditFundraisingResponse>> getTempFundraisings() {
        return ResponseEntity.ok(fundraisingAdminFacade.getTempFundraisings());
    }

    @GetMapping("/admin/temp/fundraisings/{id}")
    public ResponseEntity<FundraisingDto.EditFundraisingDto> getTempFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingAdminFacade.findTemporaryFundraisingById(id));
    }
}
