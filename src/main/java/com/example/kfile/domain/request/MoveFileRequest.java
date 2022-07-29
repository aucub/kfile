package com.example.kfile.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 移动文件请求参数
 */
@Data
@ApiModel(description = "移动文件请求")
public class MoveFileRequest {

    @ApiModelProperty(value = "父路径ID")
    private String source;

    @ApiModelProperty(value = "文件ID")
    private String fileId;

    @ApiModelProperty(value = "目标父路径")
    private String target;

}
