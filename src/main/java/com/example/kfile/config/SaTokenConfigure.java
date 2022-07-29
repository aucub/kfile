package com.example.kfile.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SaToken 权限配置
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * 注册权限校验拦截器, 拦截所有 /admin/** 请求, 但登陆相关的接口不需要认证.
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 根据路由划分模块，不同模块不同鉴权
            SaRouter.match("/admin/**", StpUtil::checkLogin);
            // 忽略所有登陆相关接口
        })).addPathPatterns("/**").excludePathPatterns("/admin/login", "/admin/login/**", "/admin");
    }

}