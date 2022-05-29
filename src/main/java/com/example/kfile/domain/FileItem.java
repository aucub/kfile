package com.example.kfile.domain;

import com.example.kfile.domain.enums.FileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("FileItem")
@ApiModel(value = "文件信息")
public class FileItem implements Serializable {

    @Id
    private String id;

    @Version
    private Long version;

    private String fileInfoId;

    @ApiModelProperty(value = "文件名", example = "a")
    private String name;

    @ApiModelProperty(value = "扩展名", example = "mp4")
    private String ext;

    @ApiModelProperty(value = "类型", example = "file")
    private FileTypeEnum type;

    @ApiModelProperty(value = "MIME类型", example = "video/mp4")
    private String contentType;

    @ApiModelProperty(value = "所在路径ID")
    private String directory;

    @ApiModelProperty(value = "创建时间", example = "2020-01-01 15:22")
    private Date createdDate;

    @ApiModelProperty(value = "修改时间", example = "2020-01-03 15:22")
    private Date lastModifiedDate;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "共享")
    private String share;

    @ApiModelProperty(value = "描述")
    private String description;

}