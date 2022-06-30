package com.example.kfile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.example.kfile.entity.enums.AclEnum;
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

    @TableId
    private Integer id;

    /**
     * 文件ID
     */
    private String fileItemId;

    /**
     * 访问范围
     */
    private AclEnum acl;

    /**
     * 权限
     */
    private String aclList;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 过期时间
     */
    private Date expireDate;

    private Date lastModifiedDate;

    private Integer createdBy;

    private Integer lastModifiedBy;
}
