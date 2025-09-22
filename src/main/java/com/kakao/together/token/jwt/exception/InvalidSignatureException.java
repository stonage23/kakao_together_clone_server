package com.kakao.together.token.jwt.exception;

public class InvalidSignatureException extends InvalidRefreshTokenException {
    public InvalidSignatureException(String message) {
        super(message);
    }

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
