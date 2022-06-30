package com.example.kfile.service.impl;

import com.example.kfile.entity.User;
import com.example.kfile.mapper.UserMapper;
import com.example.kfile.service.IRedisService;
import com.example.kfile.service.IUserCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCacheServiceImpl implements IUserCacheService {

    private IRedisService redisService;
    private UserMapper userMapper;

    @Autowired
    public void setRedisService(IRedisService redisService) {
        this.redisService = redisService;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void delUser(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            String key = "User:" + user.getUsername();
            redisService.del(key);
        }
    }

    @Override
    public User getUser(String username) {
        String key = "User:" + username;
        return (User) redisService.get(key);
    }

    @Override
    public void setUser(User user) {
        String key = "User:" + user.getUsername();
        redisService.set(key, user);
    }
}
