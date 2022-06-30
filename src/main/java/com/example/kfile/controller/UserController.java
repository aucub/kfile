package com.example.kfile.controller;

import com.example.kfile.dto.UserDto;
import com.example.kfile.entity.Result;
import com.example.kfile.entity.User;
import com.example.kfile.service.IUserService;
import com.example.kfile.service.impl.TokenServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@RestController
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

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setTokenService(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public Result register(@Validated @RequestBody UserDto userDto) throws Exception {
        userService.register(userDto);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password) {
        String token;
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
            return Result.validateError("用户名或密码不正确");
        }
        userService.updateLoginDateByUsername(username);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return Result.success(tokenMap);
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = tokenService.refresh(token);
        if (refreshToken == null) {
            return Result.error("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return Result.success(tokenMap);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestParam String username,
                                 @RequestParam String password) throws Exception {
        userService.updatePassword(username, password);
        return Result.success("密码修改成功");
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        User user = userService.getUserInfo();
        return Result.success(user);
    }


}
