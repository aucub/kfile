package com.example.kfile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String nickname;

    private String password;

    private Boolean enabled;

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

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Long getUsedUpRate() {
        return usedUpRate;
    }

    public void setUsedUpRate(Long usedUpRate) {
        this.usedUpRate = usedUpRate;
    }

    public Long getUsedDownRate() {
        return usedDownRate;
    }

    public void setUsedDownRate(Long usedDownRate) {
        this.usedDownRate = usedDownRate;
    }

    public Long getFreeUpRate() {
        return freeUpRate;
    }

    public void setFreeUpRate(Long freeUpRate) {
        this.freeUpRate = freeUpRate;
    }

    public Long getFreeDownRate() {
        return freeDownRate;
    }

    public void setFreeDownRate(Long freeDownRate) {
        this.freeDownRate = freeDownRate;
    }

    public Long getRateResetLeftMills() {
        return rateResetLeftMills;
    }

    public void setRateResetLeftMills(Long rateResetLeftMills) {
        this.rateResetLeftMills = rateResetLeftMills;
    }

    public Boolean getFreeUser() {
        return freeUser;
    }

    public void setFreeUser(Boolean freeUser) {
        this.freeUser = freeUser;
    }

    public Long getAccountExpireLeftTime() {
        return accountExpireLeftTime;
    }

    public void setAccountExpireLeftTime(Long accountExpireLeftTime) {
        this.accountExpireLeftTime = accountExpireLeftTime;
    }

    public Long getTotalUsedStorage() {
        return totalUsedStorage;
    }

    public void setTotalUsedStorage(Long totalUsedStorage) {
        this.totalUsedStorage = totalUsedStorage;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public LocalDateTime getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(LocalDateTime loginDate) {
        this.loginDate = loginDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Integer lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id = " + id +
                ", username = " + username +
                ", nickname = " + nickname +
                ", password = " + password +
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
