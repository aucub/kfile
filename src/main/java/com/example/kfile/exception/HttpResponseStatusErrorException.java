package com.example.kfile.exception;

/**
 * Http 请求状态码异常 （返回状态码为 5xx 抛出此异常）
 */
public class HttpResponseStatusErrorException extends RuntimeException {

    public HttpResponseStatusErrorException(String message) {
        super(message);
    }
}
