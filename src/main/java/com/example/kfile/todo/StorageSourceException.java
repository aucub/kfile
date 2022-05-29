package com.example.kfile.todo;

import com.example.kfile.exception.ServiceException;
import com.example.kfile.util.CodeMsg;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 存储源异常
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class StorageSourceException extends ServiceException {

    //TODO 根据需求修改
    /**
     * 存储源
     */
    private final String platform;
    /**
     * 是否使用异常消息进行接口返回，如果是则取异常的 message, 否则取 CodeMsg 中的 message
     */
    private boolean responseExceptionMessage;

    public StorageSourceException(CodeMsg codeMsg, String platform, String message) {
        super(message, codeMsg);
        this.platform = platform;
    }

    public StorageSourceException(CodeMsg codeMsg, String platform, String message, Throwable cause) {
        super(message, cause, codeMsg);
        this.platform = platform;
    }


    /**
     * 根据 responseExceptionMessage 判断使用异常消息进行接口返回，如果是则取异常的 message, 否则取 CodeMsg 中的 message
     *
     * @return 异常消息
     */
    public String getResultMessage() {
        return responseExceptionMessage ? super.getMessage() : super.getCodeMsg().getMsg();
    }


    /**
     * 设置值是否使用异常消息进行接口返回
     *
     * @param responseExceptionMessage 是否使用异常消息进行接口返回
     * @return 当前对象
     */
    public StorageSourceException setResponseExceptionMessage(boolean responseExceptionMessage) {
        this.responseExceptionMessage = responseExceptionMessage;
        return this;
    }

}