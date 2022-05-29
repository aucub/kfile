package com.example.kfile.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Service
public class DynamicControllerManager {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private RequestMappingInfo shortLinkMappingInfo;

    private Object shortLinkHandler;

    private Method shortLinkMethod;

    public void initDirectLinkPrefixPath(String path, Object handler, Method method) {
        if (shortLinkMappingInfo != null) {
            throw new RuntimeException("请勿重复初始化 DirectLinkPrefixPath.");
        }
        shortLinkMappingInfo = RequestMappingInfo.paths(path + "/{storageKey}/**").build();
        shortLinkHandler = handler;
        shortLinkMethod = method;
        requestMappingHandlerMapping.registerMapping(shortLinkMappingInfo, handler, method);
    }

    public void changeDirectLinkPrefixPath(String path) {
        if (shortLinkMappingInfo != null) {
            requestMappingHandlerMapping.unregisterMapping(shortLinkMappingInfo);
        }
        shortLinkMappingInfo = RequestMappingInfo.paths(path + "/**").build();
        requestMappingHandlerMapping.registerMapping(shortLinkMappingInfo, shortLinkHandler, shortLinkMethod);
    }

}
