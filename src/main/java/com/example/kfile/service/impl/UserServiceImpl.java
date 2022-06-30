package com.example.kfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.kfile.entity.Authority;
import com.example.kfile.entity.User;
import com.example.kfile.mapper.AuthorityMapper;
import com.example.kfile.mapper.UserMapper;
import com.example.kfile.service.IUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService, UserDetailsService {
    private UserMapper userMapper;
    private AuthorityMapper authorityMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User selectUser = new User();
        selectUser.setUsername(username);
        User user = userMapper.selectOne(new QueryWrapper<>(selectUser));
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<String> authorities = selectAuthorityByUserId(user.getId());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(authorities.size());
        authorities.forEach(name -> grantedAuthorities.add(new SimpleGrantedAuthority(name)));
        return org.springframework.security.core.userdetails.User.withUsername("user")
                .password(user.getPassword())
                .disabled(!user.getEnabled())
                .authorities(grantedAuthorities)
                .build();
    }

    /**
     * 根据用户id查询用户权限
     *
     * @param userId
     * @return
     */
    public List<String> selectAuthorityByUserId(int userId) {
        Authority authority = new Authority();
        authority.setUserId(Integer.valueOf(userId));
        List<Authority> authorities = authorityMapper.selectList(new QueryWrapper(authority));
        return authorities.stream().map(Authority::getAuthority).collect(Collectors.toList());
    }

    public void register(User user) throws Exception {
        User selectUser = new User();
        selectUser.setUsername(user.getUsername());
        selectUser.setMail(user.getMail());
        if (Objects.nonNull(userMapper.selectOne(new QueryWrapper<>(selectUser)))) {
            throw new Exception("用户名已存在！");
        }
        userMapper.insert(user);
    }

    /**
     * 根据用户名修改登录时间
     */
    private void updateLoginDateByUsername(String username) {
        userMapper.updateLoginDateByUsername(username);
    }

}
