package com.example.kfile.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilePermissionEnum {

    ACCESS("access"),
    DOWNLOAD("download"),
    UPLOAD("upload"),
    LOAD("load"),
    OWNER("owner");

    private final String value;
}
