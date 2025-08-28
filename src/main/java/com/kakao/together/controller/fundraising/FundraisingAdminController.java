package com.kakao.together.controller.fundraising;

import com.kakao.together.annotation.Admin;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest.Temporary;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest.PUBLISHED;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingStatusUpdateRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleTempFundraisingResponse;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.facade.FundraisingAdminFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/fundraisings")
public class FundraisingAdminController {

    private final FundraisingAdminFacade fundraisingAdminFacade;

    @Admin
    @PostMapping("/temp")
    public ResponseEntity<Void> processTempFundraising(@RequestBody @Validated(Temporary.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.createFundraising(request, DraftStatus.TEMPORARY);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PostMapping("")
    public ResponseEntity<Void> createFundraising(@RequestBody @Validated(PUBLISHED.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.createFundraising(request, DraftStatus.PUBLISHED);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFundraising(@PathVariable Long id, @RequestBody @Validated(PUBLISHED.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.updateFundraising(request);
        return ResponseEntity.ok().build();
    }

    @Admin
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeFundraisingStatus(@PathVariable Long id, @RequestBody FundraisingStatusUpdateRequest request) {
        fundraisingAdminFacade.changeFundraisingStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/temp")
    public ResponseEntity<List<SimpleTempFundraisingResponse>> getTempFundraisings() {
        return ResponseEntity.ok(fundraisingAdminFacade.getAllTempFundraisings());
    }

    @GetMapping("/temp/{id}")
    public ResponseEntity<EditFundraisingResponse> getTempFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingAdminFacade.getTempFundraising(id));
    }
}
