package com.example.kfile.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求参数参数
 */
@Data
public class UserLoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

}
