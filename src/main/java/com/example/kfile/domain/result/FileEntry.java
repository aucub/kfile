package com.example.kfile.domain.result;

import com.example.kfile.domain.FileItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileEntry extends FileItem {

    private String url;
    private Long size;

    @ApiModelProperty(value = "SHA256")
    private String sha256sum;
}
