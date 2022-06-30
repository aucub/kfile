package com.example.kfile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.kfile.entity.User;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
public interface IUserService extends IService<User> {

    User getUserInfo();

    String getUsername();
}
