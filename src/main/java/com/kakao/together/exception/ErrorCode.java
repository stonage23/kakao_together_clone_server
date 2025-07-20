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
    INVALID_INPUT_VALUES("INVALID_INPUT_VALUES", "입력 정보에 문제가 존재합니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_CHECKPASSWORD("NOT_MATCH_CHECKPASSWORD", "비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    FAILED_DELETE_MEMBER("FAILED_DELETE_MEMBER", "유저를 DB에서 삭제하는 과정에 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_LOGIN_INFO("INVALID_LOGIN_INFO", "아이디 또는 비밀번호를 확인해주세요", HttpStatus.NOT_FOUND),

    // 토큰
    NOT_MATCH_BEARER("NOT_MATCH_BEARER", "Bearer prefix가 없는 토큰", HttpStatus.INTERNAL_SERVER_ERROR),
    // 메일
    INTERNAL_MAIL_ERROR("INTERNAL_MAIL_ERROR", "이메일을 발송하던 중 에러가 발생하였습니다. 서버측에 문의해주세요", HttpStatus.INTERNAL_SERVER_ERROR),

    // redis
    REDIS_EXCEPTION("REDIS_EXCEPTION", "redis 데이터 조작 중 예외가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
