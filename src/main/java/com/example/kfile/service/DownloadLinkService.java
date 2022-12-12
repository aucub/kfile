package com.example.kfile.service;

import cn.hutool.core.util.StrUtil;
import com.example.kfile.exception.InvalidDirectLinkException;
import com.example.kfile.exception.StorageSourceFileOperatorException;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.templateparser.text.TextParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class DownloadLinkService {
    StorageService storageService;

    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String getDownloadLink(String platform, String filePath, int expires) throws IOException, TextParseException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 检查系统是否允许直链
        if (false) {
            throw new InvalidDirectLinkException("当前系统不允许使用直链.");
        }
        return getDownload(platform, filePath, expires);
    }

    /**
     * 处理对存储源的下载请求
     */
    private String getDownload(String platform, String filePath, int expires) throws IOException, TextParseException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 获取文件下载链接
        String downloadUrl;
        try {
            downloadUrl = storageService.getDownloadUrl(filePath, "", expires);
        } catch (StorageSourceFileOperatorException e) {
            throw new RuntimeException("获取存储源 [" + platform + "] 文件 [" + filePath + "] 下载链接异常, 无法下载.", e);
        }

        // 判断下载链接是否为空
        if (StrUtil.isEmpty(downloadUrl)) {
            throw new RuntimeException("获取存储源 [" + platform + "] 文件 [" + filePath + "] 下载链接为空, 无法下载.");
        }
        return downloadUrl;

    }

}
