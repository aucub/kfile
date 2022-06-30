package com.example.kfile.service;

public interface StorageService {
    /**
     * 获取文件下载地址
     */
    String getDownloadUrl(String path, long expires);


}
