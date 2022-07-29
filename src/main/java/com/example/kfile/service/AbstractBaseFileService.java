package com.example.kfile.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseFileService implements BaseFileService {

    /**
     * 存储源
     */
    @Getter
    private String platform;

    /**
     * TODO 测试是否连接成功, 会尝试取调用获取根路径的文件, 如果没有抛出异常, 则认为连接成功.
     */
    public void testConnection() {
    }

    String getStorageSimpleInfo() {
        return "存储源";
    }


    @Override
    public boolean copyFile(String path, String name, String targetPath, String targetName) {
        return false;
    }

    @Override
    public boolean copyFolder(String path, String name, String targetPath, String targetName) {
        return false;
    }

    @Override
    public boolean moveFile(String path, String name, String targetPath, String targetName) {
        return false;
    }

    @Override
    public boolean moveFolder(String path, String name, String targetPath, String targetName) {
        return false;
    }

}