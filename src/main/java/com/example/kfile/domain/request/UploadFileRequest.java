package com.example.kfile.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 上传文件请求参数
 */
@Data
@ApiModel(description = "上传文件请求类")
public class UploadFileRequest {

    @ApiModelProperty(value = "存储源", required = true, example = "minio-1")
    @NotBlank(message = "存储源不能为空")
    private String platform;

    @ApiModelProperty(value = "文件名", example = "a.mp4")
    private String name;

    @ApiModelProperty(value = "大小", example = "1024")
    private Long size;

    @ApiModelProperty(value = "上传路径")
    private String path;

    private String fileId;

    private String md5;

}