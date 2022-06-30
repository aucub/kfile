package com.example.kfile.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

public interface ITokenService {
    /**
     * 根据Claims生成JWT的token
     */
    String generateToken(Map<String, Object> claims, int expireTime);

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    Claims parseToken(String token);

    /**
     * 生成token的过期时间
     */
    Date generateExpirationDate(int expireTime);

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    String getUserNameFromToken(String token);

    /**
     * 从令牌中获取用户详情
     *
     * @param token 令牌
     * @return 用户详情
     */
    UserDetails getUserDetails(String token);

    /**
     * 验证token是否有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     * @return 是否有效
     */
    boolean validateToken(String token, UserDetails userDetails);

    /**
     * 判断token是否已经失效
     *
     * @param token 令牌
     * @return 是否失效
     */
    boolean isTokenExpired(String token);

    /**
     * 从token中获取过期时间
     *
     * @param token 令牌
     * @return 过期时间
     */
    Date getExpiredDateFromToken(String token);

    /**
     * 根据用户信息生成token
     *
     * @param userDetails 用户信息
     * @return 生成的token
     */
    String generateToken(UserDetails userDetails, int expireTime);

    /**
     * 刷新token
     *
     * @param oldToken 带tokenHead的旧token
     * @return 刷新后的token
     */
    String refresh(String oldToken, int expireTime);

    /**
     * 判断token在指定时间内是否刚刚刷新过
     *
     * @param token 原token
     * @param time  指定时间（秒）
     * @return 是否刚刚刷新过
     */
    boolean tokenRefreshJustBefore(String token, int time);
}
