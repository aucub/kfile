package com.example.kfile.entity.request;

import lombok.Data;

/**
 * 上传文件请求参数
 */
@Data
public class UploadFileRequest {

    //文件名
    private String name;

    //扩展名
    private String ext;

    //MIME类型
    private String contentType;

    //大小
    private Long size;

    //上传路径
    private String directory;

    private String sha256sum;

}