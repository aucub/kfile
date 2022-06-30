package com.example.kfile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检查请求
 */
@Service
public class FileAccessCheckService {

    FileService fileService;

    @Autowired
    public void setFileUtil(FileService fileService) {
        this.fileService = fileService;
    }

    public int checkFileIsInaccessible(String fileId, String userName) {
        if (fileService.getOwner(fileId).equals(userName)) {
            return 7;
        }
        return 0;
    }
}