package com.example.kfile.controller;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 处理前端首页 Controller
 */
@Controller
public class FrontIndexController {

    /**
     * 所有未找到的页面都跳转到首页, 用户解决 vue history 直接访问 404 的问题
     * 同时, 读取 index.html 文件, 修改 title 和 favicon 后返回.
     *
     * @return 转发到 /index.html
     */
    @RequestMapping(value = {"/**/{[path:[^\\.]*}", "/"})
    @ResponseBody
    public String redirect() throws IOException {
        // 读取 resources/static/index.html 文件修改 title 和 favicon 后返回
        ClassPathResource resource = new ClassPathResource("static/index.html");
        InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        //TODO  根据需求修改
        String siteName = "";
        if (StrUtil.isNotBlank(siteName)) {
            content = content.replace("<title>File</title>", "<title>" + siteName + "</title>");
        }
        //TODO  根据需求修改
        String faviconUrl = "";
        if (StrUtil.isNotBlank(faviconUrl)) {
            content = content.replace("/favicon.svg", faviconUrl);
        }

        return content;
    }

}