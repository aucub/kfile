package com.example.kfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.kfile.dto.LoginUser;
import com.example.kfile.dto.UserDto;
import com.example.kfile.entity.Authority;
import com.example.kfile.mapper.AuthorityMapper;
import com.example.kfile.mapper.LoginUserMapper;
import com.example.kfile.service.ILoginUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LoginUserServiceImpl implements ILoginUserService {

    @Autowired
    private LoginUserMapper loginUserMapper;


    @Autowired
    private AuthorityMapper authorityMapper;

    public void updateLoginDateByUsername(String username) {
        loginUserMapper.updateLoginDateByUsername(username);
    }

    public Boolean register(UserDto userDto) {
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(userDto, loginUser);
        loginUser.setEnabled(true);
        loginUser.setUsername(userDto.getMail());
        loginUser.setNickname(userDto.getNickname());
        loginUser.setPassword(userDto.getPassword());
        loginUserMapper.insert(loginUser);
        LoginUser selectUser = new LoginUser();
        selectUser.setUsername(loginUser.getUsername());
        selectUser = loginUserMapper.selectOne(new QueryWrapper<>(selectUser));
        if (Objects.isNull(selectUser)) {
            return false;
        }
        Authority authority = new Authority();
        authority.setUserId(selectUser.getId());
        authority.setAuthority("ROLE_USER");
        authorityMapper.insert(authority);
        return true;
    }

    public Boolean checkMail(String mail) {
        LoginUser selectUser = new LoginUser();
        selectUser.setMail(mail);
        if (Objects.nonNull(loginUserMapper.selectOne(new QueryWrapper<>(selectUser)))) {
            return true;
        }
        return false;
    }

    @Override
    public void updatePassword(String username, String password) {
        LoginUser selectUser = new LoginUser();
        selectUser.setUsername(username);
        LoginUser loginUser = loginUserMapper.selectOne(new QueryWrapper<>(selectUser));
        if (Objects.nonNull(loginUser)) {
        }

    }
}
