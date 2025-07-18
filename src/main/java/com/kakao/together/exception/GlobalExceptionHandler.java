package com.kakao.together.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_FILED = "message";
    private final ErrorCode INTERNAL_SERVER_ERROR = ErrorCode.INTERNAL_SERVER_ERROR;
}
