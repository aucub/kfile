package com.example.kfile.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 文件列表信息结果类
 */
@Data
@ApiModel(value = "文件列表信息结果类")
@AllArgsConstructor
public class FileInfoList {

    @ApiModelProperty(value = "文件列表")
    private List<FileInfo> files;

}