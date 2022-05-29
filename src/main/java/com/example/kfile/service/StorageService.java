package com.example.kfile.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * 获取文件下载地址
     */
    String getDownloadUrl(String path);

    String getDownloadUrl(String path, long expires);

    InputStreamResource downloadFile(String path, String filename);

    String delete(String path);

    String upload(MultipartFile file);
}
