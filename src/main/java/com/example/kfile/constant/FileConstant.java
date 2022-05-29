package com.example.kfile.constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * File 常量
 */
@Configuration
public class FileConstant {

    public static final Character PATH_SEPARATOR_CHAR = '/';

    public static final String PATH_SEPARATOR = "/";

    /**
     * 最大支持文本文件大小为 ? KB 的文件内容.
     */
    public static Long TEXT_MAX_FILE_SIZE_KB = 100L;

    @Autowired(required = false)
    public void setTextMaxFileSizeMb(@Value("${file.preview.text.maxFileSizeKb}") Long maxFileSizeKb) {
        FileConstant.TEXT_MAX_FILE_SIZE_KB = maxFileSizeKb;
    }

}