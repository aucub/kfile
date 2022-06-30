package com.example.kfile.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 删除文件请求参数
 */
@Data
public class DeleteFileRequest {

    //文件ID
    @NotBlank(message = "文件不能为空")
    private String id;
}
