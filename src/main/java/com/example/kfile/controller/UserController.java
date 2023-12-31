package com.example.kfile.controller;

import com.example.kfile.dto.UserDto;
import com.example.kfile.entity.FileItem;
import com.example.kfile.entity.Result;
import com.example.kfile.entity.Token;
import com.example.kfile.entity.User;
import com.example.kfile.entity.enums.FileTypeEnum;
import com.example.kfile.entity.request.UserLoginRequest;
import com.example.kfile.service.IFileItemService;
import com.example.kfile.service.ILoginUserService;
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

import java.util.Date;
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

    @Autowired
    IFileItemService fileItemService;

    private IUserService userService;

    private ILoginUserService loginUserService;

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private TokenServiceImpl tokenService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.expireTime}")
    private int expireTime;

    @Value("${jwt.refreshExpireTime}")
    private int refreshExpireTime;

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

    @Autowired
    public void setLoginUserService(ILoginUserService loginUserService) {
        this.loginUserService = loginUserService;
    }

    @PostMapping("/register")
    public Result register(@Validated @RequestBody UserDto userDto) {
        if (loginUserService.checkMail(userDto.getMail()))
            return Result.error("邮箱已被注册");
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Integer id = loginUserService.register(userDto);
        if (id != null) {
            FileItem fileItem = new FileItem();
            fileItem.setCreatedBy(id);
            fileItem.setName("drive");
            fileItem.setCreatedDate(new Date());
            fileItem.setType(FileTypeEnum.FOLDER);
            if (fileItemService.save(fileItem))
                return Result.success("注册成功");
        }
        return Result.error("注册失败");
    }

    @PostMapping("/login")
    public Result login(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        if (!loginUserService.checkMail(userLoginRequest.getUsername())) return Result.error("邮箱不存在");
        String rawtoken, rawRefreshToken;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userLoginRequest.getUsername());
            if (!passwordEncoder.matches(userLoginRequest.getPassword(), userDetails.getPassword())) {
                return Result.error("密码不正确");
            }
            if (!userDetails.isEnabled()) {
                return Result.error("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            rawtoken = tokenService.generateToken(userDetails, expireTime);
            rawRefreshToken = tokenService.generateToken(userDetails, refreshExpireTime);
        } catch (AuthenticationException e) {
            return Result.validateError("用户名或密码不正确");
        }
        loginUserService.updateLoginDateByUsername(userLoginRequest.getUsername());
        User user = userService.getUserInfo();
        Token token = new Token(rawtoken, expireTime);
        Token refreshToken = new Token(rawRefreshToken, refreshExpireTime);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("user", user);
        tokenMap.put("token", token);
        tokenMap.put("refreshToken", refreshToken);
        return Result.success(tokenMap);
    }

    @PostMapping("/refreshToken")
    public Result refreshToken(@RequestBody HttpServletRequest request) {
        String refreshToken = request.getHeader(tokenHeader).substring(this.tokenHead.length());
        String refreshedToken = tokenService.refresh(refreshToken, expireTime);
        if (refreshedToken == null) {
            return Result.error("refreshToken已经过期！");
        }
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", new Token(refreshedToken, expireTime));
        return Result.success(tokenMap);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestParam String username,
                                 @RequestParam String password){
        loginUserService.updatePassword(username, password);
        return Result.success("密码修改成功");
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        User user = userService.getUserInfo();
        return Result.success(user);
    }
}
