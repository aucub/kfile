package com.example.kfile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
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
public class Share implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 链接, example = "voldd3"
     */
    @TableId("url")
    private String url;

    /**
     * 文件ID
     */
    private String fileItemId;

    /**
     * 访问范围, example = "public","aclist","users"
     */
    private String acl;

    /**
     * 权限,4=access,2=download,1=upload
     */
    private String aclList;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间, example = "2021-11-22 10:05"
     */
    private Date createdDate;

    /**
     * 过期时间, example = "2021-11-23 10:05"
     */
    private Date expireDate;

    private Date lastModifiedDate;

    private Integer createdBy;

    private Integer lastModifiedBy;
}
