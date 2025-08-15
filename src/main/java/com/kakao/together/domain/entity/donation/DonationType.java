package com.kakao.together.domain.entity.donation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DonationType {
    DIRECT("DIRECT"),
    INDIRECT("INDIRECT");

    private final String value;

    @JsonCreator
    public static DonationType deserialize(String value) {
        for (DonationType type : DonationType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) return type;
        }
        throw new CustomException(ErrorCode.INVALID_ARGUMENT, "적절하지 않은 DonationType: " + value);
    }
}
