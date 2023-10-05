package com.example.kfile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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

    private String username;

    private String nickname;

    private transient String password;

    private Boolean enabled;

    @TableLogic
    private Integer deleted;

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
    private LocalDateTime loginDate;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private Integer lastModifiedBy;

    private LocalDateTime lastModifiedDate;

    @Override
    public String toString() {
        return "User{" +
                "id = " + id +
                ", username = " + username +
                ", nickname = " + nickname +
                ", password = [PROTECTED]" +
                ", enabled = " + enabled +
                ", mail = " + mail +
                ", usedUpRate = " + usedUpRate +
                ", usedDownRate = " + usedDownRate +
                ", freeUpRate = " + freeUpRate +
                ", freeDownRate = " + freeDownRate +
                ", rateResetLeftMills = " + rateResetLeftMills +
                ", freeUser = " + freeUser +
                ", accountExpireLeftTime = " + accountExpireLeftTime +
                ", totalUsedStorage = " + totalUsedStorage +
                ", loginIp = " + loginIp +
                ", loginDate = " + loginDate +
                ", createdBy = " + createdBy +
                ", createdDate = " + createdDate +
                ", lastModifiedBy = " + lastModifiedBy +
                ", lastModifiedDate = " + lastModifiedDate +
                "}";
    }
}
