package com.kakao.together.external.redis.exception;

public class RedisServiceException extends  RuntimeException {
    public RedisServiceException(String message) { super(message);
    }
    public RedisServiceException(String message, Throwable cause) { super(message, cause); }
}
