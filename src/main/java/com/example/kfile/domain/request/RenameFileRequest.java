package com.example.kfile.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重命名文件请求参数
 */
@Data
@ApiModel(description = "重命名文件请求类")
public class RenameFileRequest {

    @ApiModelProperty(value = "文件ID")
    @NotBlank(message = "文件不能为空")
    private String fileId;

    @ApiModelProperty(value = "重命名后的文件名称", example = "text-1.txt")
    @NotBlank(message = "新文件名不能为空")
    private String newName;
}
