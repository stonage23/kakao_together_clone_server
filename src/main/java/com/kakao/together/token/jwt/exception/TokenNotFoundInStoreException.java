package com.kakao.together.token.jwt.exception;

public class TokenNotFoundInStoreException extends InvalidRefreshTokenException{
    public TokenNotFoundInStoreException(String message) {
        super(message);
    }

    public TokenNotFoundInStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
