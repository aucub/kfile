package com.example.kfile.domain;

import com.example.kfile.domain.enums.FileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("FileInfo")
@ApiModel(value = "文件信息")
public class FileInfo implements Serializable {

    @Id
    private String id;

    @ApiModelProperty(value = "存储源", example = "minio-1")
    private String storage;

    @ApiModelProperty(value = "文件名", example = "a.mp4")
    private String name;

    @ApiModelProperty(value = "类型", example = "file")
    private FileTypeEnum type;

    @ApiModelProperty(value = "大小", example = "1024")
    private Long size;

    @ApiModelProperty(value = "所在路径ID")
    private String path;

    @ApiModelProperty(value = "创建时间", example = "2020-01-01 15:22")
    private Date createdDate;

    @ApiModelProperty(value = "修改时间", example = "2020-01-03 15:22")
    private Date lastModifiedDate;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "共享")
    private String share;

    @ApiModelProperty(value = "描述")
    private String describe;

}