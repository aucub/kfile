package com.example.kfile.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上传文件请求参数
 */
@Data
@ApiModel(description = "上传文件请求类")
public class UploadFileRequest {

    @ApiModelProperty(value = "文件名", example = "a")
    private String name;

    @ApiModelProperty(value = "扩展名", example = "mp4")
    private String ext;

    @ApiModelProperty(value = "MIME类型", example = "video/mp4")
    private String contentType;

    @ApiModelProperty(value = "大小", example = "1024")
    private Long size;

    @ApiModelProperty(value = "上传路径")
    private String directory;

    private String fileItemId;

    private String sha256sum;

}