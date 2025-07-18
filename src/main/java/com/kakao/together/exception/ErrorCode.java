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

    // 유저
    NOT_FOUND_USER("NOT_FOUND_USER", "유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_PROFILE("NOT_FOUND_PROFILE", "프로필이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "중복된 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_INPUT_VALUES("INVALID_INPUT_VALUES", "입력 정보에 문제가 존재합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
