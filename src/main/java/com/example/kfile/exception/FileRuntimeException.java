package com.example.kfile.exception;

public class FileRuntimeException extends RuntimeException {

    public FileRuntimeException(String message) {
        super(message);
    }

    public FileRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
