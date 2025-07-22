package com.kakao.together.jwt;

import com.kakao.together.auth.JwtAuthExceptions;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Jwt token을 Dispatcher-Servlet 이하 layer에서 사용할 경우 사용
 */
@RestControllerAdvice(basePackages = "com.kakao.together.jwt")
@Slf4j
public class JwtExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Void> handleException(JwtException e) {
        log.error("##### JwtException 예외 핸들러: " + e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<Void> unsupportedJwtException(UnsupportedJwtException e) {
        log.error("##### UnsupportedJwtException 예외 핸들러: " + e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> illegalArgumentException(IllegalArgumentException e) {
        log.error("##### illegalArgumentException 예외 핸들러: " + e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(JwtAuthExceptions.RefreshTokenUserNotFoundException.class)
    public ResponseEntity<Void> refreshTokenUserNotFoundException(JwtAuthExceptions.RefreshTokenUserNotFoundException e) {
        log.error("##### RefreshTokenUserNotFoundException 예외 핸들러: " + e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> exception(Exception e) {
        log.error("##### Exception 예외 핸들러: " + e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
