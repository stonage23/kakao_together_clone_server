package com.kakao.together.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 테스트 에러 */
    TEST_ERROR("TEST_ERROR", "test ErrorCode", HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 에러이므로 서버 팀에 연락주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    END(null, null, null),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
