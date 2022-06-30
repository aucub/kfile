package com.example.kfile.entity;

import com.example.kfile.entity.enums.FilePermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Acl {
    private Integer id;
    private FilePermissionEnum permission;
}
