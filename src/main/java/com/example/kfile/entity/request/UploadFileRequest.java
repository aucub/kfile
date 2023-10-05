package com.example.kfile.entity.request;

import lombok.Data;

/**
 * 上传文件请求参数
 */
@Data
public class UploadFileRequest {

    //文件名", example = "a
    private String name;

    //扩展名", example = "mp4
    private String ext;

    //MIME类型", example = "video/mp4
    private String contentType;

    //大小", example = "1024
    private Long size;

    //上传路径
    private String directory;

    private String fileItemId;

    private String sha256sum;

}