package com.example.kfile.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    /**
     * 文件
     */
    FILE("FILE"),

    /**
     * 文件夹
     */
    FOLDER("FOLDER");

    @JsonValue
    private final String value;

}