package com.kakao.together.controller.admin;

import com.kakao.together.annotation.Admin;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest.DRAFT;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest.PUBLISHED;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingResponse;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingStatusUpdateRequest;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.SimpleDraftFundraisingResponse;
import com.kakao.together.domain.entity.fundraising.DraftStatus;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.facade.FundraisingAdminFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/fundraisings")
@Slf4j
public class FundraisingAdminController {

    private final FundraisingAdminFacade fundraisingAdminFacade;

    @Admin
    @PostMapping("/draft")
    // TODO [Refactor] 생성, 수정 dto 나누기
    public ResponseEntity<Void> processDraftFundraising(@RequestBody @Validated(DRAFT.class) EditFundraisingRequest request) {
        fundraisingAdminFacade.createFundraising(request, DraftStatus.DRAFT);
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
        if (id != request.getFundraisingId()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        fundraisingAdminFacade.updateFundraising(request);
        return ResponseEntity.ok().build();
    }

    @Admin
    @GetMapping("/{id}")
    public ResponseEntity<EditFundraisingResponse> getFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingAdminFacade.getFundraising(id));
    }

    @Admin
    @PatchMapping("/{id}/fundraising-status")
    public ResponseEntity<Void> changeFundraisingStatus(@PathVariable Long id, @RequestBody FundraisingStatusUpdateRequest request) {fundraisingAdminFacade.changeFundraisingStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/draft")
    public ResponseEntity<List<SimpleDraftFundraisingResponse>> getAllDraftFundraising() {
        return ResponseEntity.ok(fundraisingAdminFacade.getAllDraftFundraising());
    }

    @GetMapping("/draft/{id}")
    public ResponseEntity<EditFundraisingResponse> getDraftFundraising(@PathVariable Long id) {
        return ResponseEntity.ok(fundraisingAdminFacade.getDraftFundraising(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFundraising(@PathVariable Long id) {
        fundraisingAdminFacade.deleteFundraising(id);
        return ResponseEntity.ok().build();
    }
}
