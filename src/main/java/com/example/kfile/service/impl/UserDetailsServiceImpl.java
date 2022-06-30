package com.example.kfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.kfile.dto.LoginUser;
import com.example.kfile.entity.Authority;
import com.example.kfile.mapper.AuthorityMapper;
import com.example.kfile.mapper.LoginUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        LoginUser selectUser = new LoginUser();
        selectUser.setUsername(username);
        LoginUser loginUser = loginUserMapper.selectOne(new QueryWrapper<>(selectUser));
        List<String> authorities = selectAuthorityByUserId(loginUser.getId());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(authorities.size());
        authorities.forEach(name -> grantedAuthorities.add(new SimpleGrantedAuthority(name)));
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(loginUser.getPassword())
                .disabled(!loginUser.getEnabled())
                .authorities(grantedAuthorities)
                .build();
    }

    /**
     * 根据用户id查询用户权限
     */
    public List<String> selectAuthorityByUserId(int userId) {
        Authority authority = new Authority();
        authority.setUserId(userId);
        List<Authority> authorities = authorityMapper.selectList(new QueryWrapper<>(authority));
        return authorities.stream().map(Authority::getAuthority).collect(Collectors.toList());
    }
}
