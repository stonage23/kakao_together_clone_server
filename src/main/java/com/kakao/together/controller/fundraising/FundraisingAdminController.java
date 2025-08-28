package com.kakao.together.controller.fundraising;

import com.kakao.together.annotation.Admin;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest.Save;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest.Update;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingStatusUpdateRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleEditFundraisingResponse;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
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
    public ResponseEntity<Void> processTempFundraising(@RequestBody @Validated(Save.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.createFundraising(request, DraftStatus.TEMPORARY);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PostMapping("/admin/fundraisings")
    public ResponseEntity<Void> createFundraising(@RequestBody @Validated(Save.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.createFundraising(request, DraftStatus.CREATED);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PutMapping("/admin/fundraisings/{id}")
    public ResponseEntity<Void> updateFundraising(@PathVariable Long id, @RequestBody @Validated(Update.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.updateFundraising(request);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PatchMapping("/admin/fundraisings/{id}/status")
    public ResponseEntity<Void> changeFundraisingStatus(@PathVariable Long id, @RequestBody FundraisingStatusUpdateRequest request) {
        fundraisingAdminFacade.changeFundraisingStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/temp/fundraisings")
    public ResponseEntity<List<SimpleEditFundraisingResponse>> getTempFundraisings() {
        return ResponseEntity.ok(fundraisingAdminFacade.getAllTempFundraisings());
    }

    @GetMapping("/admin/temp/fundraisings/{id}")
    public ResponseEntity<EditFundraisingResponse> getTempFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingAdminFacade.getTempFundraising(id));
    }
}
