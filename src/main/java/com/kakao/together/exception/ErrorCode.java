package com.kakao.together.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 테스트 에러 */
    TEST_ERROR("test ErrorCode", "TEST_ERROR", HttpStatus.BAD_REQUEST),

    INTERNAL_SERVER_ERROR("서버 에러이므로 서버 팀에 연락주세요.", "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED_REQUEST("인증된 계정만 접근이 가능한 요청입니다.", "UNAUTHENTICATED_REQUEST", HttpStatus.FORBIDDEN),
    /**
     * [case] 조회한 엔티티가 존재하지 않는 경우 <br>
     * [format] 요청한 엔티티가 존재하지 않습니다; userId: 1234
     */
    NOT_FOUND_ENTITY("요청한 Entity가 존재하지 않습니다.", "NOT_FOUND_ENTITY", HttpStatus.NOT_FOUND),
    /**
     * [case] 전달받은 인수가 허용하는 값 이외의 값인 경우 <br>
     * [format] 적절하지 않은 OOO: {}
      */
    INVALID_ARGUMENT("적절하지 않은 입력값입니다.", "INVALID_ARGUMENT", HttpStatus.BAD_REQUEST),
    NOT_NULL_VIOLATION("null이면 안되는 엔티티 제약조건을 위반하였습니다.", "NOT_NULL_VIOLATION", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_PERMITTED_CONDITION("허용한 조건 분기 외 로직 호출예외가 발생하였습니다.", "NOT_PERMITTED_CONDITION", HttpStatus.INTERNAL_SERVER_ERROR),
    BUISINESS_VIOLATION("비즈니스룰 위반입니다.", "BUISINESS_VIOLATION", HttpStatus.BAD_REQUEST),
    CODE_EXPIRED("인증 코드가 만료되었습니다. 다시 시도해주세요.", "CODE_EXPIRED", HttpStatus.NOT_FOUND),

    // 유저
    NOT_FOUND_USER("유저가 존재하지 않습니다.", "NOT_FOUND_USER", HttpStatus.NOT_FOUND),
    NOT_FOUND_PROFILE("프로필이 존재하지 않습니다.", "NOT_FOUND_PROFILE", HttpStatus.NOT_FOUND),
    NOT_FOUND_PWD_CODE("비밀번호 변경 토큰이 존재하지 않습니다.", "NOT_FOUND_PWD_CODE", HttpStatus.GONE),
    NOT_AUTHENTICATE_USER("인증된 유저가 보낸 요청이 아닙니다.", "NOT_AUTHENTICATE_USER", HttpStatus.FORBIDDEN),
    DUPLICATE_EMAIL("중복된 이메일입니다.", "DUPLICATE_EMAIL", HttpStatus.CONFLICT),
    INVALID_INPUT_VALUES("입력 정보에 문제가 존재합니다.", "INVALID_INPUT_VALUES", HttpStatus.BAD_REQUEST),
    NOT_MATCH_CHECKPASSWORD("비밀번호 확인이 일치하지 않습니다.", "NOT_MATCH_CHECKPASSWORD", HttpStatus.BAD_REQUEST),
    NOT_MATCH_PASSWORD("틀린 비밀번호입니다.", "NOT_MATCH_PASSWORD", HttpStatus.NOT_FOUND),
    FAILED_DELETE_MEMBER("유저를 DB에서 삭제하는 과정에 문제가 발생하였습니다.", "FAILED_DELETE_MEMBER", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_LOGIN_INFO("아이디 또는 비밀번호를 확인해주세요", "INVALID_LOGIN_INFO", HttpStatus.NOT_FOUND),
    ACCESS_DENIED("해당 작업을 하기위한 권한이 없습니다.", "ACCESS_DENIED", HttpStatus.FORBIDDEN),

    // 모금 포스트
    NOT_FOUND_FUNDRAISING("요청하신 모금을 찾을 수 없습니다.", "NOT_FOUND_FUNDRAISING", HttpStatus.NOT_FOUND),

    // 관리자
    ONLY_ADMIN_EXCEPTION("Admin 계정만 접근할 수 있는 요청입니다.", "ONLY_ADMIN_EXCEPTION", HttpStatus.FORBIDDEN),

    // 파일
    NOT_FOUND_VALUE("필요한 값이 존재하지 않습니다.", "NOT_FOUND_VALUE", HttpStatus.BAD_REQUEST),
    NOT_VALID_FORMAT("사용가능한 파일 확장자가 아닙니다.", "NOT_VALID_FORMAT", HttpStatus.BAD_REQUEST),
    // TODO 구체적인 예외처리
    FAILED_UPLOAD_FILE("파일 업로드에 실패했습니다", "FAILED_UPLOAD_FILE", HttpStatus.INTERNAL_SERVER_ERROR),
    // TODO 구체적인 예외처리
    FAILED_DELETE_FILE("파일 삭제에 실패했습니다.", "FAILED_DELETE_FILE", HttpStatus.INTERNAL_SERVER_ERROR),

    // 토큰
    NOT_MATCH_BEARER("Bearer prefix가 없는 토큰", "NOT_MATCH_BEARER", HttpStatus.INTERNAL_SERVER_ERROR),
    // 메일
    INTERNAL_MAIL_ERROR("이메일을 발송하던 중 에러가 발생하였습니다. 서버측에 문의해주세요", "INTERNAL_MAIL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    // redis
    REDIS_EXCEPTION("redis 데이터 조작 중 예외가 발생하였습니다.", "REDIS_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),

    // 결제
    DUPLICATE_PAYMENT("해당 merchantUid을 가진 결제 내역이 이미 DB에 존재합니다.", "DUPLICATE_PAYMENT", HttpStatus.CONFLICT),
    FAILED_VERIFY_PAYMENT("PG사 결제 검증에 실패하였습니다. 서버 관리자에게 문의해주세요", "FAILED_VERIFY_PAYMENT", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_PAYMENT_CANCEL("결제 취소에 실패했습니다. 관리자에게 문의해주세요", "FAILED_PAYMENT_CANCEL", HttpStatus.INTERNAL_SERVER_ERROR),

    // html 파싱
    NOT_VALID_TAG("적절하지 않은 tag가 포함되어 있습니다.", "NOT_VALID_TAG", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;
}
