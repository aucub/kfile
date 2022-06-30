package com.example.kfile.service.impl;

import com.example.kfile.service.ITokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Slf4j
@Service
public class TokenServiceImpl implements ITokenService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expireTime}")
    private int expireTime;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 根据Claims生成JWT的token
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(generateExpirationDate())
                .signWith(new SecretKeySpec(Decoders.BASE64.decode(secret), "HmacSHA512"))
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(new SecretKeySpec(Decoders.BASE64.decode(secret), "HmacSHA512"))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 生成token的过期时间
     */
    public Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expireTime * 1000L);
    }

    public String getUserNameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public UserDetails getUserDetails(String token) {
        Claims claims = parseToken(token);
        String username = claims.getSubject();
        // 获取用户的权限信息，这里假设权限信息已经存储在claims中的authorities字段中
        Object authoritiesObj = claims.get("authorities");
        if (authoritiesObj instanceof Collection<?> authoritiesCollection) {
            boolean isSafe = true;
            for (Object authority : authoritiesCollection) {
                if (!(authority instanceof GrantedAuthority)) {
                    isSafe = false;
                    break;
                }
            }
            if (isSafe) {
                @SuppressWarnings("unchecked")
                Collection<? extends GrantedAuthority> authorities = (Collection<? extends GrantedAuthority>) authoritiesCollection;
                // 在这里可以使用 authorities 进行安全的操作
                return User.withUsername(username)
                        .authorities(authorities)
                        .build();
            }
        }
        // 处理 authorities 不是集合的情况
        return null;
    }

    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && userDetails.isEnabled() && userDetails.isAccountNonExpired() && userDetails.isAccountNonLocked() && userDetails.isCredentialsNonExpired();
    }

    /**
     * 判断token是否已经失效
     */
    public boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     */
    public Date getExpiredDateFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 根据用户信息生成token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("iat", new Date());
        claims.put("authorities", userDetails.getAuthorities());
        return generateToken(claims);
    }

    /**
     * 当原来的token没过期时是可以刷新的
     *
     * @param oldToken 带tokenHead的token
     */
    public String refresh(String oldToken) {
        if (StringUtils.hasLength(oldToken)) {
            return null;
        }
        String token = oldToken.replace(tokenHead, "");
        if (StringUtils.hasLength(token)) {
            return null;
        }
        // Token校验不通过
        Claims claims = parseToken(token);
        if (Objects.isNull(claims)) {
            return null;
        }
        // 如果token已经过期，不支持刷新
        if (isTokenExpired(token)) {
            return null;
        }
        // 如果token在30分钟之内刚刷新过，返回原token
        if (tokenRefreshJustBefore(token, 30 * 60)) {
            return token;
        } else {
            claims.put("iat", new Date());
            return generateToken(claims);
        }
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     *
     * @param token 原token
     * @param time  指定时间（秒）
     * @return 是否刚刚刷新过
     */
    public boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = parseToken(token);
        Date created = claims.getIssuedAt();
        Date refreshDate = new Date();
        // 刷新时间在创建时间的指定时间内
        return refreshDate.after(created) && refreshDate.before(new Date(created.getTime() + time * 1000L));
    }
}