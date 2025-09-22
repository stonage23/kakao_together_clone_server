package com.kakao.together.token.jwt.exception;

public class ExpiredTokenException extends InvalidRefreshTokenException{

    public ExpiredTokenException(String message) {
        super(message);
    }

    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
