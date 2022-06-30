package com.example.kfile.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@TableName("user")
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Email
    private String username;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+-={}|;':\",.<>?]+$", message = "符号仅支持!@#$%^&*()_+-={}|;':\",.<>?")
    @Size(min = 8, max = 22, message = "密码长度应在8-22位之间")
    private String password;

    @TableLogic
    private Boolean enabled;

    @NotBlank(message = "用户名不能为空")
    @Email
    private String mail;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    private Date createdDate;

    private Date lastModifiedDate;
}
