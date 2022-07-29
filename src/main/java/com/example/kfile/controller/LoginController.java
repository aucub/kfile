package com.example.kfile.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.example.kfile.domain.request.UserLoginRequest;
import com.example.kfile.util.AjaxJson;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 登陆注销相关接口
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @PostMapping("/login")
    public AjaxJson<?> doLogin(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        StpUtil.login(userLoginRequest.getUsername());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return AjaxJson.getSuccess("登录成功", tokenInfo.getTokenValue());
        // return AjaxJson.getError("登录失败, 账号或密码错误");
    }

    @PostMapping("/logout")
    public AjaxJson<?> logout() {
        StpUtil.logout();
        return AjaxJson.getSuccess("注销成功");
    }

    @GetMapping("/login/check")
    public AjaxJson<Boolean> checkLogin() {
        return AjaxJson.getSuccessData(StpUtil.isLogin());
    }

}
