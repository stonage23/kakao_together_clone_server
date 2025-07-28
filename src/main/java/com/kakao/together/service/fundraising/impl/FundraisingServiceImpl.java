package com.kakao.together.service.fundraising.impl;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.FundraisingResponse;
import com.kakao.together.controller.fundraising.dto.Status;
import com.kakao.together.domain.entity.fundraising.Fundraising;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.repository.FundraisingRepository;
import com.kakao.together.service.fundraising.FundraisingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FundraisingServiceImpl implements FundraisingService {

    private final FundraisingRepository fundraisingRepository;

    @Override
    public Fundraising createTempFundraising(Fundraising fundrasing) {
        return fundraisingRepository.save(fundrasing);
    }

    @Override
    public Fundraising createFundraising(Fundraising fundrasing) {
        return fundraisingRepository.save(fundrasing);
    }

    @Override
    public Optional<Fundraising> findFundraisingNullable(Long fundraisingId) {
        return fundraisingRepository.findById(fundraisingId);
    }

    @Override
    public Fundraising findFundraisingNullCheck(Long fundraisingId) {
        return fundraisingRepository.findById(fundraisingId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 fundraisingId을 가진 모금이 없습니다.")
        );
    }

    @Override
    public List<FundraisingResponse> findFundraisingsExpiringInThreeDaysLimit(int limit) {
        return fundraisingRepository.findFundraisingsExpiringInThreeDaysLimit(limit).stream()
                .map(fundraising -> FundraisingResponse.fromEntity(fundraising))
                .collect(Collectors.toList());
    }

    @Override
    public List<FundraisingResponse> findFundraisingsTopLimit(int limit) {
        return fundraisingRepository.findFundraisingsTopLimit(limit).stream()
                .map(fundraising -> FundraisingResponse.fromEntity(fundraising))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteIfExists(Long fundraisingId) {
        if (!fundraisingRepository.existsById(fundraisingId)) {
            log.warn("존재하지 않는 모금Id로 데이터 삭제 시도; fundraisingId: {}, method: {}", fundraisingId, "FundraisingServiceImpl.existsById");
            return;
        }
        fundraisingRepository.deleteById(fundraisingId);
    }

    @Override
    public FundraisingResponse getOngoingFundraisingResponse(Long fundraisingId) {
        return fundraisingRepository.findByIdAndStatus(fundraisingId, Status.ONGOING)
                .map(FundraisingResponse::fromEntity)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 fundraisingId을 가진 모금이 없습니다."));
    }

    @Override
    public Fundraising findTempFundraisingById(Long fundraisingId) {
        return fundraisingRepository.findByIdAndStatus(fundraisingId, Status.TEMPORARY).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "임시 저장 모금이 존재하지 않습니다.")
        );
    }

    @Override
    public List<Fundraising> findAllTempFundraisings() {
        return fundraisingRepository.findByStatus(Status.TEMPORARY);
    }
}
