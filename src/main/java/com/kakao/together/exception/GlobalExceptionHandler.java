package com.kakao.together.exception;

import com.kakao.together.token.jwt.exception.InvalidRefreshTokenException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
    }

    // @Valid 유효성 검증 실패 케이스
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug("입력 데이터 유효성 검증 실패");
        e.getBindingResult().getAllErrors().stream().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            log.debug(fieldName + " : " + error.getDefaultMessage());
        });
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUES, e.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // @Validated 유효성 검증 실패 케이스
    @ExceptionHandler({ ConstraintViolationException.class })
    protected ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
        log.debug("입력 데이터 유효성 검증 실패");
        e.getConstraintViolations().stream().forEach(cv -> {
            log.debug(cv.getPropertyPath().toString());
        });
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUES, e.getConstraintViolations());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 커스텀 예외
    // NOTE 커스텀 예외는 다중으로 wrap하지 않기
    @ExceptionHandler({ CustomException.class })
    protected ResponseEntity<ErrorResponse> customException(CustomException e) {
//        if (e.getCause() != null) {
//            log.warn(e.getMessage(), e);
//        } else {
//            StackTraceElement[] stackTraceElements = e.getStackTrace();
//            log.info(stackTraceElements[0].toString() + " : " + e.getClass() + " : "+ e.getMessage());
//        }
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

    @ExceptionHandler({InvalidRefreshTokenException.class})
    protected ResponseEntity<ErrorResponse> invalidRefreshTokenException(InvalidRefreshTokenException e) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        log.info(stackTraceElements[0].toString() + " : " + e.getClass() + " : "+ e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 핸들링되지 않은 예외
    @ExceptionHandler({ Exception.class })
    protected  ResponseEntity<ErrorResponse> exception(Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        log.error(e.getMessage(), e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
