package com.example.kfile.service;

import com.example.kfile.dto.UserDto;

public interface ILoginUserService {

    void updateLoginDateByUsername(String username);

    Boolean register(UserDto userDto);

    Boolean checkMail(String mail);

    void updatePassword(String username, String password);
}
