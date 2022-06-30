package com.example.kfile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.kfile.entity.User;
import com.example.kfile.mapper.UserMapper;
import com.example.kfile.service.IUserCacheService;
import com.example.kfile.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private UserMapper userMapper;

    private IUserCacheService userCacheService;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUserCacheService(IUserCacheService userCacheService) {
        this.userCacheService = userCacheService;
    }


    @Override
    public User getUserInfo() {
        User cacheUser = userCacheService.getUser(getUsername());
        if (Objects.nonNull(cacheUser)) {
            return cacheUser;
        }
        User selectUser = new User();
        selectUser.setUsername(getUsername());
        User user = userMapper.selectOne(new QueryWrapper<>(selectUser));
        userCacheService.setUser(user);
        return user;
    }

    public String getUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}
