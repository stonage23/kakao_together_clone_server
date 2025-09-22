package com.kakao.together.exception.file;

public class FileIOException extends FileException {
    public FileIOException(String message) {
        super(message);
    }

    public FileIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
