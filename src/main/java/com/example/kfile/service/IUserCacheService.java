package com.example.kfile.service;

import com.example.kfile.entity.User;

public interface IUserCacheService {
    /**
     * 删除用户缓存
     */
    void delUser(Integer userId);

    /**
     * 获取用户缓存
     */
    User getUser(String username);

    /**
     * 设置用户缓存
     */
    void setUser(User user);

}
