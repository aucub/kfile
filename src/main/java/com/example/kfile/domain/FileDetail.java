package com.example.kfile.domain;

import cn.xuyanwu.spring.file.storage.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("FileDetail")
public class FileDetail extends FileInfo {

    @ApiModelProperty(value = "SHA256")
    private String sha256sum;

    @ApiModelProperty(value = "创建者")
    private String createdBy;
}
