package com.example.kfile.exception;

public class FileRetryException extends RuntimeException {

    public FileRetryException() {
    }

    public FileRetryException(String message) {
        super(message);
    }

    public FileRetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileRetryException(Throwable cause) {
        super(cause);
    }

    public FileRetryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
