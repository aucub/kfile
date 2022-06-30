package com.example.kfile.security;

import com.example.kfile.entity.enums.FilePermissionEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilePermissionCheck {
    FieldPermission[] value();

    @interface FieldPermission {
        String field();

        FilePermissionEnum permission();
    }
}
