package com.example.kfile.exception;

import com.example.kfile.util.CodeMsg;
import lombok.Getter;

/**
 * 存储源文件操作异常
 */
@Getter
public class StorageSourceFileOperatorException extends StorageSourceException {

    public StorageSourceFileOperatorException(CodeMsg codeMsg, String platform, String message, Throwable cause) {
        super(codeMsg, platform, message, cause);
    }

}
