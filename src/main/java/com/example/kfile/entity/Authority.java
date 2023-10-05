package com.example.kfile.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
@Data
public class Authority implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String authority;

    @Override
    public String toString() {
        return "Authority{" +
                "userId = " + userId +
                ", authority = " + authority +
                "}";
    }
}
