package com.example.kfile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDto {
    @NotEmpty
    private String password;
    @Email
    private String mail;
    private String nickname;
}
