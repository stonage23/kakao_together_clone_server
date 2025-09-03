package com.kakao.together.token.jwt.exception;

public class JwtAuthExceptions {

    public static class RefreshTokenUserNotFoundException extends RuntimeException {
        public RefreshTokenUserNotFoundException(String message) {super(message);}
    }

    public static class BedCredentialsException extends RuntimeException {
        public BedCredentialsException(String message) {super(message);}
    }
}
