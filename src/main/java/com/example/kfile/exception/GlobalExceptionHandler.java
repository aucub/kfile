package com.example.kfile.exception;

import cn.hutool.core.io.IORuntimeException;
import com.example.kfile.todo.StorageSourceException;
import com.example.kfile.todo.StorageSourceFileOperatorException;
import com.example.kfile.util.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.FileNotFoundException;

/**
 * 全局异常处理
 */
@ControllerAdvice
@Slf4j
@Order(1)
public class GlobalExceptionHandler {


    /**
     * 存储源文件操作相关异常处理
     */
    @ExceptionHandler(value = StorageSourceException.class)
    @ResponseBody
    @ResponseStatus
    public AjaxJson<String> storageSourceException(StorageSourceException e) {
        return AjaxJson.getError(e.getResultMessage());
    }

    /**
     * 存储源文件操作相关异常处理
     */
    @ExceptionHandler(value = StorageSourceFileOperatorException.class)
    @ResponseBody
    @ResponseStatus
    public AjaxJson<String> storageSourceFileOperatorException(StorageSourceFileOperatorException e) {
        return AjaxJson.getError(e.getResultMessage());
    }

    @ExceptionHandler({FileNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxJson<String> fileNotFound() {
        return AjaxJson.getError("文件不存在");
    }

    /**
     * 密码校验异常
     */
    @ExceptionHandler({PasswordVerifyException.class})
    @ResponseBody
    @ResponseStatus
    public AjaxJson<?> passwordVerifyException(PasswordVerifyException ex) {
        return AjaxJson.get(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxJson<String> handlerHttpMessageNotReadableException(HttpMessageNotReadableException ignoredE) {
        return AjaxJson.getBadRequestError("请求参数不合法");
    }

    /**
     * 捕获 ClientAbortException 异常, 不做任何处理, 防止出现大量堆栈日志输出, 此异常不影响功能.
     */
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class, ClientAbortException.class})
    @ResponseBody
    @ResponseStatus
    public void clientAbortException() {
    }


    /**
     * 捕获 IORuntimeException 异常, 如果 cause 是 ClientAbortException, 不做任何处理, 防止出现大量堆栈日志输出, 此异常不影响功能.
     */
    @ExceptionHandler({IORuntimeException.class})
    @ResponseBody
    @ResponseStatus
    public void ioRuntimeException(IORuntimeException e) {
        if (e.getCause() instanceof ClientAbortException) {
            return;
        }
        log.error(e.getMessage(), e);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus
    public AjaxJson<String> extraExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);

        if (e.getClass() == Exception.class) {
            return AjaxJson.getError("系统异常");
        } else {
            return AjaxJson.getError(e.getMessage());
        }
    }


}