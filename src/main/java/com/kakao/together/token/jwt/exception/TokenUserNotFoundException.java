package com.kakao.together.token.jwt.exception;

public class TokenUserNotFoundException extends InvalidRefreshTokenException{
    public TokenUserNotFoundException(String message) {
        super(message);
    }

    public TokenUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
