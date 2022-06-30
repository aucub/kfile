package com.example.kfile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.kfile.entity.enums.FileTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author aucub
 * @since 2023-11-12
 */
@Data
@TableName("file_item")
public class FileItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private String id;

    private String url;

    private String name;

    private String ext;

    private FileTypeEnum type;

    private String contentType;

    private String directory;

    private Date createdDate;

    private Date lastModifiedDate;

    private Integer createdBy;

    private Integer lastModifiedBy;

    private String share;

    private String description;
}
