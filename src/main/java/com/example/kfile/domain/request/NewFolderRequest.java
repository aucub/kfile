package com.example.kfile.domain.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewFolderRequest {
    @ApiModelProperty(value = "文件名", example = "a.mp4")
    private String name;

    @ApiModelProperty(value = "所在路径ID")
    private String path;
}
