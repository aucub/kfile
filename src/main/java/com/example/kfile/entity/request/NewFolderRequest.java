package com.example.kfile.entity.request;

import lombok.Data;

@Data
public class NewFolderRequest {
    //文件名
    private String name;

    //所在路径ID
    private String directory;
}
