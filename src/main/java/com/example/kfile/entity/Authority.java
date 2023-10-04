package com.example.kfile.entity;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String authority;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "userId = " + userId +
                ", authority = " + authority +
                "}";
    }
}
