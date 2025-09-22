package com.kakao.together.exception.file;

public abstract class FileException extends RuntimeException{

    protected FileException(String message) { super(message); }

    protected FileException(String message, Throwable cause) { super(message, cause); }
}
