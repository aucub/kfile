package com.example.kfile.exception;


import com.example.kfile.util.CodeMsg;

/**
 * 无效的存储源异常
 */
public class InvalidStorageSourceException extends StorageSourceException {

    public InvalidStorageSourceException(String message) {
        super(CodeMsg.STORAGE_SOURCE_NOT_FOUND, null, message);
    }

    public InvalidStorageSourceException(String platform, String message) {
        super(CodeMsg.STORAGE_SOURCE_NOT_FOUND, platform, message);
    }

}
