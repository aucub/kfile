package com.example.kfile.entity.request;

import lombok.Data;


/**
 * 复制文件请求参数
 */
@Data
public class CopyFileRequest {

    //文件ID
    private String id;

    //目标路径
    private String target;

}
