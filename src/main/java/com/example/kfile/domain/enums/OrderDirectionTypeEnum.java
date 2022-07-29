package com.example.kfile.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderDirectionTypeEnum {
    /**
     * 升序
     */
    ASC("asc"),

    /**
     * 降序
     */
    DESC("desc");

    @JsonValue
    private final String value;
}
