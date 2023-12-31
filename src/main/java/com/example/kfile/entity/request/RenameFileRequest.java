package com.example.kfile.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重命名文件请求参数
 */
@Data
public class RenameFileRequest {

    //文件ID
    @NotBlank(message = "文件不能为空")
    private String id;

    //重命名后的文件名称
    @NotBlank(message = "新文件名不能为空")
    private String newName;
}
