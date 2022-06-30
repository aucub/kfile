package com.example.kfile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
 * @since 2023-10-04
 */
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Email
    private String username;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private Boolean enabled;

    @TableLogic
    private Integer deleted;

    @Email
    private String mail;

    /**
     * 已用上传流量
     */
    private Long usedUpRate;

    /**
     * 已用下载流量
     */
    private Long usedDownRate;

    /**
     * 可用上传流量
     */
    private Long freeUpRate;

    /**
     * 可用下载流量
     */
    private Long freeDownRate;

    /**
     * 流量重置时间
     */
    private Long rateResetLeftMills;

    /**
     * 是否是免费用户
     */
    private Boolean freeUser;

    /**
     * 账号过期时间
     */
    private Long accountExpireLeftTime;

    /**
     * 已用存储空间
     */
    private Long totalUsedStorage;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    private Integer createdBy;

    private Date createdDate;

    private Integer lastModifiedBy;

    private Date lastModifiedDate;
}
