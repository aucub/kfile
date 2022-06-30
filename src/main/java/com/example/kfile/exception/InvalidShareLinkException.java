package com.example.kfile.exception;

/**
 * 无效的分享异常
 */
public class InvalidShareLinkException extends FileRuntimeException {

    public InvalidShareLinkException(String message) {
        super(message);
    }

}
