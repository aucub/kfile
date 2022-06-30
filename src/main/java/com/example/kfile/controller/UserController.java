package com.example.kfile.controller;

import com.example.kfile.entity.Result;
import com.example.kfile.entity.User;
import com.example.kfile.service.IUserService;
import com.example.kfile.service.impl.TokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author aucub
 * @since 2023-10-04
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private IUserService userService;

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private TokenServiceImpl tokenService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @PostMapping("/register")
    public Result register(@RequestBody User user) throws Exception {
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                return Result.error("密码不正确");
            }
            if (!userDetails.isEnabled()) {
                return Result.error("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = tokenService.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.error("登录异常，detail" + e.getMessage());
            return Result.error("用户名或密码不正确");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return Result.success(tokenMap);
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = tokenService.refresh(token);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return Result.success(tokenMap);
    }

}
