package com.example.kfile.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AclEnum {
    PUBLIC("public"),
    ACL("acl"),
    USER("user");

    private final String value;
}
