package com.kakao.together.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String DEFAULT_FILED = "message";
    private final ErrorCode INTERNAL_SERVER_ERROR = ErrorCode.INTERNAL_SERVER_ERROR;

    // @Valid 유효성 검증 실패 케이스
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("##### 전역 예외처리:  MethodArgumentNotValidException");
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUES, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // @Validated 유효성 검증 실패 케이스
    @ExceptionHandler({ ConstraintViolationException.class })
    protected ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
        log.info("##### 전역 예외처리 : ConstraintViolationException");
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUES, e.getConstraintViolations());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ CustomException.class })
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.info("##### 전역 예외처리 : CustomException; {}: {}", e.getErrorCode(), e.getMessage());
        ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler({ Exception.class })
    protected  ResponseEntity<ErrorResponse> exception(Exception e) {
        log.info("##### 전역 예외처리 : Exception");
        log.error("예상치 못한 예외 발생: {}; {}", e.getClass(), e.getMessage());
        ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
