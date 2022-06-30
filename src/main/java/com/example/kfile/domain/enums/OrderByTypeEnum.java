package com.example.kfile.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderByTypeEnum {

    /**
     * 文件名
     */
    NAME("name"),

    /**
     * 大小
     */
    SIZE("size"),
    /**
     * 时间
     */
    TIME("time");

    @JsonValue
    private final String value;
}
