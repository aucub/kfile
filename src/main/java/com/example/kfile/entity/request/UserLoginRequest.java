package com.example.kfile.entity.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求参数参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Email
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+-={}|;':\",.<>?]+$", message = "符号仅支持!@#$%^&*()_+-={}|;':\",.<>?")
    @Size(min = 8, max = 22, message = "密码长度应在8-22位之间")
    private String password;

}
