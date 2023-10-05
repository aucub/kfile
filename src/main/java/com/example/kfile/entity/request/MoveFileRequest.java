package com.example.kfile.entity.request;

import lombok.Data;

/**
 * 移动文件请求参数
 */
@Data
public class MoveFileRequest {

    //父路径ID
    private String source;

    //文件ID
    private String fileItemId;

    //目标父路径
    private String target;

}
