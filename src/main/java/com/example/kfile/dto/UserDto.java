package com.example.kfile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+-={}|;':\",.<>?]+$", message = "符号仅支持!@#$%^&*()_+-={}|;':\",.<>?")
    @Size(min = 8, max = 22, message = "密码长度应在8-22位之间")
    private String password;
    @Email
    private String mail;
    @NotBlank(message = "昵称不能为空")
    private String nickname;
}
