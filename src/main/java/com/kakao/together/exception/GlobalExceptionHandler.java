package com.kakao.together.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @Valid 유효성 검증 실패 케이스
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug("입력 데이터 유효성 검증 실패", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUES, e.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // @Validated 유효성 검증 실패 케이스
    @ExceptionHandler({ ConstraintViolationException.class })
    protected ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
        log.debug("입력 데이터 유효성 검증 실패", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUES, e.getConstraintViolations());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 커스텀 예외
    // NOTE 커스텀 예외는 다중으로 wrap하지 않기
    @ExceptionHandler({ CustomException.class })
    protected ResponseEntity<ErrorResponse> customException(CustomException e) {
        log.error(e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(e);
        return ResponseEntity
                .status(response.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler({ NullPointerException.class })
    protected  ResponseEntity<ErrorResponse> nullPointerException(NullPointerException e) {
        log.error(e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 핸들링되지 않은 런타임 예외
    @ExceptionHandler({ RuntimeException.class })
    protected  ResponseEntity<ErrorResponse> runtimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 핸들링되지 않은 예외
    @ExceptionHandler({ Exception.class })
    protected  ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error(e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
