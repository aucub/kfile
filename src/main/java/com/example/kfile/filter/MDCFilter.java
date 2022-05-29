package com.example.kfile.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.example.kfile.constant.MdcConstant;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        MDC.put(MdcConstant.TRACE_ID, IdUtil.fastUUID());

        //TODO  根据实际情况修改

        // MDC.put(MdcConstant.IP, ServletUtil.getClientIP(httpServletRequest));
        MDC.put(MdcConstant.USER, StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "anonymous");

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.clear();
        }
    }

}