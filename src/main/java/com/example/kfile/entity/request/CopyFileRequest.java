package com.example.kfile.entity.request;

import lombok.Data;


/**
 * 复制文件请求参数
 */
@Data
public class CopyFileRequest {

    //"父路径ID"
    private String source;

    //"文件ID"
    private String fileItemId;

    //"目标路径"
    private String target;

}
