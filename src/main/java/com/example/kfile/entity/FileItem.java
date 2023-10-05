package com.example.kfile.entity;

import com.example.kfile.entity.enums.FileTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileItem implements Serializable {

    @Id
    private String id;

    @Version
    private short version;

    private String fileInfoId;

    //"文件名", example = "a.mp4"
    private String name;

    //"扩展名", example = "mp4"
    private String ext;

    //"类型", example = "file"
    private FileTypeEnum type;

    //"MIME类型", example = "video/mp4"
    private String contentType;

    //"所在路径ID"
    private String directory;

    //"创建时间", example = "2020-01-01 15:22"
    private Date createdDate;

    //"修改时间", example = "2020-01-03 15:22"
    private Date lastModifiedDate;

    //"创建者"
    private String createdBy;

    //"共享"
    private String share;

    //"描述"
    private String description;

}